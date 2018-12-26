package cat.xlagunas.conference.data

import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.Conferencee
import cat.xlagunas.conference.domain.IceCandidateMessage
import cat.xlagunas.conference.domain.SessionMessage
import cat.xlagunas.conference.domain.UserSessionIdentifier
import cat.xlagunas.conference.domain.WsMessagingWrapper
import com.google.gson.Gson
import com.tinder.scarlet.WebSocket
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class ConferenceRepositoryImp @Inject constructor(
    private val messagingApiWrapper: WsMessagingWrapper,
    private val peerConnectionFactory: PeerConnectionFactory,
    private val rtcConfiguration: PeerConnection.RTCConfiguration,
    private val webRTCEventHandler: WebRTCEventHandler,
    private val userSessionIdentifier: UserSessionIdentifier

) : ConferenceRepository {

    private val peerConnectionMap = ConcurrentHashMap<String, PeerConnection>()
    private val peerConnectionConstraints = MediaConstraints().apply {
        mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        mandatory.add(
            MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", "true"
            )
        )
    }
    //TODO Should be passed as dependency
    private val temporalGson = Gson()

    override fun joinRoom() {
        Timber.d("Handling state for current user on phone: ${userSessionIdentifier.getUserId()}")
        messagingApiWrapper.setupWebSocketListeners()

        GlobalScope.launch {
            messagingApiWrapper.observeMessageEvent()
                .filter { it is WebSocket.Event.OnConnectionOpened<*> }
                .consumeEach {
                    messagingApiWrapper.sendMessage(
                        MessageDto(
                            userSessionIdentifier.getUserId(),
                            "",
                            MessageType.ROOM_DISCOVERY,
                            "SERVER"
                        )
                    )
                }
        }

        GlobalScope.launch(Dispatchers.IO) {
            val users = messagingApiWrapper.getRoomParticipants.receive()
            for (user in users.users) {
                if (user.userId != userSessionIdentifier.getUserId()) {
                    Timber.d("New User found in room, user: ${user.userId}")
                    val peerConnection = createPeerConnection(user.userId)
                    if (peerConnection != null) {
                        createOffer(user.userId)
                    }
                }
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            messagingApiWrapper.observeSessionStream
                .consumeEach { message ->
                    if (message.second == MessageType.OFFER) {
                        createPeerConnection(message.first.receiver)
                        handleRemoteOffer(message.first.receiver, message.first.sessionDescription)
                    } else {
                        handleRemoteAnswer(message.first.receiver, message.first.sessionDescription)
                    }
                }
        }

        GlobalScope.launch(Dispatchers.IO) {
            messagingApiWrapper.observeIceCandidateStream
                .consumeEach { message ->
                    Timber.d("New message received: $message.data")
                    addIceCandidate(message.receiver, message.iceCandidate)

                }
        }

        GlobalScope.launch(Dispatchers.IO) {
            webRTCEventHandler.iceCandidateHandler
                .consumeEach {
                    messagingApiWrapper.sendMessage(
                        MessageDto(
                            userSessionIdentifier.getUserId(),
                            temporalGson.toJson(IceCandidateMessage(it.second, userSessionIdentifier.getUserId())),
                            MessageType.ICE_CANDIDATE,
                            it.first
                        )
                    )
                }
        }
    }

    private fun addIceCandidate(contactId: String, iceCandidate: IceCandidate) {
        Timber.d("Adding Ice candidate for $contactId")
        peerConnectionMap[contactId]?.addIceCandidate(iceCandidate)
    }

    private fun createPeerConnection(contactId: String): PeerConnection? {
        Timber.d("Creating peer connection per $contactId")
        if (peerConnectionMap[contactId] != null) {
            Timber.w("Requesting creation of a peer whose already in the list")
            return null
        }
        val peerObserver = VivPeerConnectionObserver(contactId, webRTCEventHandler)
        val peerConnection = peerConnectionFactory.createPeerConnection(rtcConfiguration, peerObserver)
        if (peerConnection != null) {
            peerConnectionMap[contactId] = peerConnection
        }
        return peerConnection
    }

    private fun createOffer(contactId: String) {
        Timber.d("Creating offer for $contactId")
        val sdpObserver = object : VivSdpObserver(contactId) {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                //TODO MAYBE IT IS NOT NEEDED
                peerConnectionMap[contactId]?.setLocalDescription(NoOPVivSdpObserver(contactId), sessionDescription)
                messagingApiWrapper.sendMessage(
                    MessageDto(
                        userSessionIdentifier.getUserId(),
                        temporalGson.toJson(SessionMessage(sessionDescription, userSessionIdentifier.getUserId())),
                        MessageType.OFFER,
                        contactId
                    )
                )
            }
        }
        peerConnectionMap[contactId]?.createOffer(sdpObserver, peerConnectionConstraints)
    }

    private fun handleRemoteOffer(contactId: String, sessionDescription: SessionDescription) {
        Timber.d("Handling remote offer from $contactId")
        val peerConnection = peerConnectionMap[contactId]!!
        val sdpObserver = object : VivSdpObserver(contactId) {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                Timber.d("Sending answer message to $contactId")
                //TODO MAYBE MAKES NO SENSE
                peerConnection.setLocalDescription(NoOPVivSdpObserver(contactId), sessionDescription)
                messagingApiWrapper.sendMessage(
                    MessageDto(
                        userSessionIdentifier.getUserId(),
                        temporalGson.toJson(SessionMessage(sessionDescription, userSessionIdentifier.getUserId())),
                        MessageType.ANSWER,
                        this.contactId
                    )
                )
            }
        }
        peerConnection.setRemoteDescription(NoOPVivSdpObserver(contactId), sessionDescription)
        peerConnection.createAnswer(sdpObserver, peerConnectionConstraints)
    }

    private fun handleRemoteAnswer(contactId: String, sessionDescription: SessionDescription) {
        Timber.d("Handling remote answer for $contactId")
        val peerConnection = peerConnectionMap[contactId]!!
        peerConnection.setRemoteDescription(NoOPVivSdpObserver(contactId), sessionDescription)
    }

    override fun getConnectedUsers(): Flowable<Conferencee> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun logoutRoom() {
        Timber.d("Logging out room!")
    }

    class NoOPVivSdpObserver(val userId: String) : VivSdpObserver(userId) {
        override fun onCreateSuccess(sessionDescription: SessionDescription) {
            Timber.i("onCreateSuccess called for $userId")
        }
    }
}