package cat.xlagunas.conference.data

import cat.xlagunas.conference.data.MediaDataSourceImp.Companion.VIDEO_TRACK_ID
import cat.xlagunas.conference.data.dto.MessageDto
import cat.xlagunas.conference.data.dto.mapper.ConferenceeMapper
import cat.xlagunas.conference.data.dto.mapper.MessageDtoMapper
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.PeerConnectionDataSource
import cat.xlagunas.conference.domain.model.Conferencee
import cat.xlagunas.conference.domain.model.MessageType
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import cat.xlagunas.conference.domain.model.SessionMessage
import cat.xlagunas.conference.domain.utils.UserSessionIdentifier
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack
import timber.log.Timber
import javax.inject.Inject

class ConferenceRepositoryImp @Inject constructor(
    private val messagingApiWrapper: WsMessagingWrapper,
    private val webRTCEventHandler: WebRTCEventHandler,
    private val userSessionIdentifier: UserSessionIdentifier,
    private val conferenceeMapper: ConferenceeMapper,
    private val messageDtoMapper: MessageDtoMapper,
    private val peerConnectionDataSource: PeerConnectionDataSource,
    private val mediaSourceDataSource: MediaDataSourceImp

) : ConferenceRepository {

    //TODO THIS SHOULD BE PROVIDED THROUGH VIEWMODEL BC IT CAN CHANGE AT ANY TIME
    private val peerConnectionConstraints = MediaConstraints().apply {
        mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
    }

    override fun joinRoom() {
        messagingApiWrapper.setupWebSocketListeners()

        observeRemoteSessions()

        observeRemoteIceCandidates()

        emitGeneratedIceCandidates()
    }

    override suspend fun getConnectedUsers(): ReceiveChannel<List<Conferencee>> {
        return messagingApiWrapper.getRoomParticipants.openSubscription().map { conferenceeMapper.fromDto(it) }
    }

    fun observeRemoteIceCandidates() {
        GlobalScope.launch(Dispatchers.IO) {
            messagingApiWrapper.observeIceCandidateStream
                .consumeEach { message ->
                    Timber.d("New message received: $message.data")
                    addIceCandidate(message.receiver, message.iceCandidate)
                }
        }
    }

    fun observeRemoteSessions() {
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
    }

    override suspend fun registerUser() {
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

    override fun logoutRoom() {
        Timber.d("Logging out room!")
    }

    private fun emitGeneratedIceCandidates() {
        GlobalScope.launch(Dispatchers.IO) {
            webRTCEventHandler.iceCandidateHandler
                .consumeEach {
                    messagingApiWrapper.sendMessage(messageDtoMapper.createIceCandidateMessage(it.second, it.first))
                }
        }
    }

    private fun addIceCandidate(contactId: String, iceCandidate: IceCandidate) {
        Timber.d("Adding Ice candidate for $contactId")
        peerConnectionDataSource.getPeerConnection(contactId).addIceCandidate(iceCandidate)
    }

    override fun createPeerConnection(userId: String): PeerConnection? {
        val peer = peerConnectionDataSource.createPeerConnection(userId)
        val videoCapturer = mediaSourceDataSource.createLocalVideoCapturer(mediaSourceDataSource.getCameraEnumerator())
        val localVideoTrack = mediaSourceDataSource.createVideoTrack(videoCapturer!!)
        peer?.addTrack(localVideoTrack, listOf(VIDEO_TRACK_ID))
        val remoteVideoTrack =  getRemoteVideoTrack(peer!!)
        remoteVideoTrack.setEnabled(true)
        remoteVideoTrack.addSink(mediaSourceDataSource.remoteLocalVideoSink)
        return peer
    }

    override fun createOffer(userId: String) {
        peerConnectionDataSource.createOffer(userId, peerConnectionConstraints) { sessionDescription ->
            val sessionMessage = SessionMessage(sessionDescription, userSessionIdentifier.getUserId())
            val messageDto = messageDtoMapper.createMessageDto(sessionMessage, MessageType.OFFER, userId)
            messagingApiWrapper.sendMessage(messageDto)
        }
    }

    private fun handleRemoteOffer(contactId: String, sessionDescription: SessionDescription) {
        peerConnectionDataSource.handleRemoteOffer(contactId, sessionDescription, peerConnectionConstraints) {
            Timber.d("Sending answer message to $contactId")
            val sessionMessage = SessionMessage(sessionDescription, userSessionIdentifier.getUserId())
            val answerDto = messageDtoMapper.createMessageDto(sessionMessage, MessageType.ANSWER, contactId)
            messagingApiWrapper.sendMessage(answerDto)
        }
    }

    private fun handleRemoteAnswer(contactId: String, sessionDescription: SessionDescription) {
        peerConnectionDataSource.handleRemoteAnswer(contactId, sessionDescription)
    }

    override fun getLocalRenderer(): ProxyVideoSink {
        return mediaSourceDataSource.proxyLocalVideoSink
    }

    override fun getRemoteRenderer(): ProxyVideoSink {
        return mediaSourceDataSource.remoteLocalVideoSink
    }


    fun getRemoteVideoTrack(peerConnection: PeerConnection) : VideoTrack{
        return peerConnection.transceivers.filter { it.mediaType == MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO }.first().receiver.track() as VideoTrack
    }
}