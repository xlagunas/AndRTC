package cat.xlagunas.conference.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import cat.xlagunas.ws_messaging.SignalingProtocol
import cat.xlagunas.ws_messaging.model.AnswerMessage
import cat.xlagunas.ws_messaging.model.IceCandidateMessage
import cat.xlagunas.ws_messaging.model.OfferMessage
import cat.xlagunas.ws_messaging.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class ConferenceViewModel @Inject constructor(
    private val conferenceRepository: ConferenceRepository,
    private val eglContext: EglBase.Context,
    private val signaling: SignalingProtocol,
    private val roomId: String
) : ViewModel() {

    val conferenceAttendees by lazy { MutableLiveData<Int>() }
    val localStream by lazy { MutableLiveData<ProxyVideoSink>() }
    val remoteStream by lazy { MutableLiveData<ProxyVideoSink>() }
    val requestedMedia = MutableLiveData<MediaConstraints>()

    private val totalConnectedUsers = AtomicInteger()

    private val iceCandidateChannel = Channel<Pair<Session, IceCandidate>>(0)

    fun getEGLContext(): EglBase.Context {
        return eglContext
    }

    fun onStart(userConstraints: MediaConstraints) {
        viewModelScope.launch(Dispatchers.IO) {
            launch { handleIncomingOfferRequests(userConstraints) }
            launch { handleIncomingAnswerRequests() }

            conferenceRepository.onNewUser()
                .onEach { session -> createPeerConnection(session) }
                .flatMapMerge { conferenceRepository.createOffer(it, userConstraints) }
                .onEach {
                    conferenceAttendees.postValue(totalConnectedUsers.incrementAndGet())
                    signaling.sendOffer(OfferMessage(it.first, it.second))
                }
                .catch { Timber.e(it) }
                .launchIn(this)

            conferenceRepository.joinRoom(roomId)
        }

        localStream.postValue(conferenceRepository.getLocalRenderer())
        remoteStream.postValue(conferenceRepository.getRemoteRenderer())
    }

    private suspend fun handleIncomingAnswerRequests() = coroutineScope {
        signaling.onReceiveAnswer()
            .onEach {
                conferenceRepository.handleRemoteAnswer(it.receiver, it.answer) {
                    launch { handleIncomingIceCandidateRequests() }
                    consumeIceCandidates()
                }
            }.launchIn(this)
    }

    private suspend fun handleIncomingIceCandidateRequests() = coroutineScope {
        signaling.onReceiveIceCandidate()
            .onEach {
                conferenceRepository.addIceCandidate(it.receiver, it.iceCandidate)
            }.launchIn(this)
    }

    private suspend fun handleIncomingOfferRequests(userConstraints: MediaConstraints) =
        coroutineScope {
            signaling.onReceiveOffer()
                .onEach { offer -> createPeerConnection(offer.receiver) }
                .flatMapMerge {
                    conferenceRepository.handleRemoteOffer(
                        it.receiver,
                        userConstraints,
                        it.offer
                    )
                }
                .onEach {
                    signaling.sendAnswer(AnswerMessage(it.first, it.second))
                    launch { handleIncomingIceCandidateRequests() }
                    consumeIceCandidates()
                }.launchIn(this)
        }

    private fun CoroutineScope.createPeerConnection(session: Session) {
        conferenceRepository.createPeerConnection(session) {
            launch { iceCandidateChannel.send(it) }
        }
    }

    private fun CoroutineScope.consumeIceCandidates() {
        launch {
            iceCandidateChannel.consumeEach {
                signaling.sendIceCandidate(IceCandidateMessage(it.first, it.second))
            }
        }
    }

    override fun onCleared() {
        conferenceRepository.logoutRoom()
    }
}