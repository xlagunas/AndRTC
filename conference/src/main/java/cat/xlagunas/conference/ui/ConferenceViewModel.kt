package cat.xlagunas.conference.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import cat.xlagunas.ws_messaging.SignalingProtocol
import cat.xlagunas.ws_messaging.model.AnswerMessage
import cat.xlagunas.ws_messaging.model.IceCandidateMessage
import cat.xlagunas.ws_messaging.model.OfferMessage
import cat.xlagunas.ws_messaging.model.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class ConferenceViewModel @Inject constructor(
    private val conferenceRepository: ConferenceRepository,
    private val eglContext: EglBase.Context,
    private val signaling: SignalingProtocol,
    private val roomId: String
) : ViewModel() {

    private val jobs = mutableListOf<Job>()

    val conferenceAttendees by lazy { MutableLiveData<Int>() }
    val localStream by lazy { MutableLiveData<ProxyVideoSink>() }
    val remoteStream by lazy { MutableLiveData<ProxyVideoSink>() }
    val requestedMedia = MutableLiveData<MediaConstraints>()

    private val totalConnectedUsers = AtomicInteger()

    fun getEGLContext(): EglBase.Context {
        return eglContext
    }

    fun onStart(userConstraints: MediaConstraints) {
        jobs += GlobalScope.launch(Dispatchers.IO) {
            launch { handleIncomingOfferRequests(userConstraints) }
            launch { handleIncomingAnswerRequests() }

            conferenceRepository.onNewUser()
                .onEach { session -> createPeerConnection(session) }
                .flatMapMerge { conferenceRepository.createOffer(it, userConstraints) }
                .onEach {
                    conferenceAttendees.postValue(totalConnectedUsers.incrementAndGet())
                    signaling.sendOffer(OfferMessage(it.first, it.second))
                }
                .launchIn(this)

            conferenceRepository.joinRoom(roomId)
        }

        localStream.postValue(conferenceRepository.getLocalRenderer())
        remoteStream.postValue(conferenceRepository.getRemoteRenderer())
    }

    private suspend fun handleIncomingAnswerRequests() {
        signaling.onReceiveAnswer()
            .collect {
                conferenceRepository.handleRemoteAnswer(it.receiver, it.answer) {
                    //TODO CHECK IF THIS CAN BE MOVED observeRemoteIceCandidates()
                    //TODO CHECK IF THIS CAN BE MOVED emitGeneratedIceCandidates()
                    GlobalScope.launch { handleIncomingIceCandidateRequests() }
                }
            }
    }

    private suspend fun handleIncomingIceCandidateRequests() {
        signaling.onReceiveIceCandidate()
            .collect {
                conferenceRepository.addIceCandidate(it.receiver, it.iceCandidate)
            }
    }

    private suspend fun handleIncomingOfferRequests(userConstraints: MediaConstraints) {
        signaling.onReceiveOffer()
            .onEach { offer -> createPeerConnection(offer.receiver) }
            .flatMapMerge {
                conferenceRepository.handleRemoteOffer(
                    it.receiver,
                    userConstraints,
                    it.offer
                )
            }
            .collect {
                //TODO CHECK IF THIS CAN BE MOVED observeRemoteIceCandidates()
                //TODO CHECK IF THIS CAN BE MOVED emitGeneratedIceCandidates()
                GlobalScope.launch { handleIncomingIceCandidateRequests() }
                signaling.sendAnswer(AnswerMessage(it.first, it.second))
            }
    }

    private fun createPeerConnection(session: Session) {
        conferenceRepository.createPeerConnection(session) {
            signaling.sendIceCandidate(IceCandidateMessage(it.first, it.second))
        }
    }

    override fun onCleared() {
        conferenceRepository.logoutRoom()
        jobs.forEach {
            if (it.isActive) {
                it.cancel()
            }
        }
    }
}