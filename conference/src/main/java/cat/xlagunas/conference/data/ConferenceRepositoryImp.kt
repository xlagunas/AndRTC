package cat.xlagunas.conference.data

import cat.xlagunas.conference.data.VivPeerConnectionObserver.SessionDescriptionState
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.Conferencee
import cat.xlagunas.conference.domain.SessionMessage
import cat.xlagunas.conference.domain.WsMessagingApi
import com.google.gson.Gson
import com.tinder.scarlet.WebSocket
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.launch
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class ConferenceRepositoryImp @Inject constructor(
    private val messagingApi: WsMessagingApi,
    private val peerConnectionFactory: PeerConnectionFactory,
    private val rtcConfiguration: PeerConnection.RTCConfiguration,
    private val webRTCEventHandler: VivPeerConnectionObserver.WebRTCEventHandler
) : ConferenceRepository {

    private val peerConnectionMap = ConcurrentHashMap<String, PeerData>()
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

    override fun joinRoom() {

        GlobalScope.launch {
            messagingApi.observeMessageEvent()
                .filter { it is WebSocket.Event.OnConnectionOpened<*> }
                .consumeEach {
                    messagingApi.sendMessage(
                        MessageDto(
                            "",
                            "Hello from Android",
                            MessageType.SERVER,
                            "JOIN"
                        )
                    )
                }

        }

        GlobalScope.launch(Dispatchers.IO) {
            val users = messagingApi.getRoomParticipants().receive()
            for (user in users) {
                Timber.d("New User found in room, user: ${user.userId}")
                val peerConnection = createPeerConnection(user.userId, SessionDescriptionState.OFFER)
                peerConnection?.createOffer(peerConnectionMap[user.userId]?.sdpObserver, peerConnectionConstraints)
            }
//TODO remove this and move it to the proper converter
            val temporalGson = Gson()
            webRTCEventHandler.sessionDescriptionHandler.consumeEach {
                when (it.third) {
                    SessionDescriptionState.OFFER -> {
                        peerConnectionMap[it.first]?.peerConnection?.setLocalDescription(
                            peerConnectionMap[it.first]?.sdpObserver,
                            it.second
                        )
                        messagingApi.sendMessage(
                            MessageDto(
                                "",
                                temporalGson.toJson(SessionMessage(it.second, it.first)),
                                MessageType.SESSION_DESCRIPTION,
                                it.first
                            )
                        )
                    }
                    SessionDescriptionState.ANSWER -> {
                        peerConnectionMap[it.first]?.peerConnection?.setRemoteDescription(
                            peerConnectionMap[it.first]?.sdpObserver,
                            it.second
                        )
                        messagingApi.sendMessage(
                            MessageDto(
                                "",
                                temporalGson.toJson(SessionMessage(it.second, it.first)),
                                MessageType.SESSION_DESCRIPTION,
                                it.first
                            )
                        )
                    }
                }
            }




            messagingApi.observeSessionStream()
                .consumeEach { message ->
                    Timber.d("New message received: $message.data")
                    val sessionDescriptionState =
                        if (message.sessionDescription.type == SessionDescription.Type.OFFER) {
                            SessionDescriptionState.OFFER
                        } else {
                            SessionDescriptionState.ANSWER
                        }

                    createPeerConnection(message.receiver, sessionDescriptionState)
                    //TODO not really happy of the double direction this allows. Ideally we should only read from this value, not update it directly, otherwise this is an eventBus
                    webRTCEventHandler.sessionDescriptionHandler.send(
                        Triple(
                            message.receiver,
                            message.sessionDescription,
                            sessionDescriptionState
                        )
                    )

                }

            messagingApi.observeIceCandidateStream()
                .consumeEach { message ->
                    Timber.d("New message received: $message.data")
                    webRTCEventHandler.iceCandidateHandler.send(Pair(message.receiver, message.iceCandidate))

                }
        }
    }

    private fun createPeerConnection(contactId: String, sessionDescription: SessionDescriptionState): PeerConnection? {
        val peerObserver = VivPeerConnectionObserver(contactId, webRTCEventHandler)
        val peerConnection = peerConnectionFactory.createPeerConnection(rtcConfiguration, peerObserver)
        peerConnection?.let {
            val sdpObserver = VivSdpObserver(contactId, webRTCEventHandler, sessionDescription)
            peerConnectionMap[contactId] = PeerData(peerConnection, sdpObserver)
        } ?: Timber.w("Attempt to create peer connection returned null for contact $contactId")
        return peerConnection
    }

    override fun getConnectedUsers(): Flowable<Conferencee> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun logoutRoom() {
        Timber.d("Logging out room!")
    }
}