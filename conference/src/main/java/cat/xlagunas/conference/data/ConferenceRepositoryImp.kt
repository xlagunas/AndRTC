package cat.xlagunas.conference.data

import cat.xlagunas.conference.data.MediaDataSourceImp.Companion.VIDEO_TRACK_ID
import cat.xlagunas.conference.data.dto.ConnectedUser
import cat.xlagunas.conference.data.dto.mapper.ConferenceeMapper
import cat.xlagunas.conference.data.dto.mapper.MessageDtoMapper
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.PeerConnectionDataSource
import cat.xlagunas.conference.domain.model.MessageType
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import cat.xlagunas.conference.domain.model.SessionMessage
import cat.xlagunas.conference.domain.utils.UserSessionIdentifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
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

    override fun joinRoom(onReceiveOfferMediaConstraints: MediaConstraints) {
        messagingApiWrapper.setupWebSocketListeners()
        observeRemoteSessions(onReceiveOfferMediaConstraints)
    }

    override suspend fun onNewUser(): ReceiveChannel<ConnectedUser> {
        return messagingApiWrapper.getNewJoiners.openSubscription()
    }

    private fun observeRemoteIceCandidates() {
        GlobalScope.launch(Dispatchers.IO) {
            messagingApiWrapper.observeIceCandidateStream
                    .consumeEach { message ->
                        Timber.d("New message received: $message.data")
                        addIceCandidate(message.receiver, message.iceCandidate)
                    }
        }
    }

    private fun observeRemoteSessions(offeredMediaConstraints: MediaConstraints) {
        GlobalScope.launch(Dispatchers.IO) {
            messagingApiWrapper.observeSessionStream
                    .consumeEach { message ->
                        if (message.second == MessageType.OFFER) {
                            createPeerConnection(message.first.receiver)
                            handleRemoteOffer(message.first.receiver, offeredMediaConstraints, message.first.sessionDescription)
                        } else {
                            handleRemoteAnswer(message.first.receiver, message.first.sessionDescription)
                        }
                    }
        }
    }

    override suspend fun registerUser() {
    }

    override fun logoutRoom() {
        Timber.d("Logging out room!")
    }

    private fun emitGeneratedIceCandidates() {
        GlobalScope.launch(Dispatchers.IO) {
            webRTCEventHandler.iceCandidateHandler
                    .consumeEach {
                        messagingApiWrapper.sendMessage("DIRECT_MESSAGE", messageDtoMapper.createIceCandidateMessage(it.second, it.first))
                    }
        }
    }

    private fun addIceCandidate(contactId: String, iceCandidate: IceCandidate) {
        Timber.d("Adding Ice candidate for $contactId")
        peerConnectionDataSource.getPeerConnection(contactId)?.addIceCandidate(iceCandidate)
    }

    override fun createPeerConnection(userId: String): PeerConnection? {
        val peer = peerConnectionDataSource.createPeerConnection(userId)
        val videoCapturer = mediaSourceDataSource.createLocalVideoCapturer(mediaSourceDataSource.getCameraEnumerator())
        val localVideoTrack = mediaSourceDataSource.createVideoTrack(videoCapturer!!)

        if (peer != null) {
            peer.addTrack(localVideoTrack, listOf(VIDEO_TRACK_ID))
            setupRemoteTrack(peer)
        } else {
            Timber.w("PeerConnection was not correctly instantiated, returned null on creation")
        }
        return peer
    }

    private fun setupRemoteTrack(peer: PeerConnection) {
        val remoteVideoTrack = getRemoteVideoTrack(peer)
        remoteVideoTrack.setEnabled(true)
        remoteVideoTrack.addSink(mediaSourceDataSource.remoteLocalVideoSink)
    }

    override fun createOffer(userId: String, mediaConstraints: MediaConstraints) {
        peerConnectionDataSource.createOffer(userId, mediaConstraints) { sessionDescription ->
            val sessionMessage = SessionMessage(sessionDescription, userSessionIdentifier.getUserId())
            val messageDto = messageDtoMapper.createMessageDto(sessionMessage, MessageType.OFFER, userId)
            messagingApiWrapper.sendMessage("DIRECT_MESSAGE", messageDto)
        }
    }

    private fun handleRemoteOffer(contactId: String, mediaConstraints: MediaConstraints, sessionDescription: SessionDescription) {
        peerConnectionDataSource.handleRemoteOffer(contactId, sessionDescription, mediaConstraints) { answer ->
            Timber.d("Sending answer message to $contactId")
            val sessionMessage = SessionMessage(answer, userSessionIdentifier.getUserId())
            val answerDto = messageDtoMapper.createMessageDto(sessionMessage, MessageType.ANSWER, contactId)
            messagingApiWrapper.sendMessage("DIRECT_MESSAGE", answerDto)
            observeRemoteIceCandidates()
            emitGeneratedIceCandidates()
        }
    }

    private fun handleRemoteAnswer(contactId: String, sessionDescription: SessionDescription) {
        peerConnectionDataSource.handleRemoteAnswer(contactId, sessionDescription) {
            observeRemoteIceCandidates()
            emitGeneratedIceCandidates()
        }
    }

    override fun getLocalRenderer(): ProxyVideoSink {
        return mediaSourceDataSource.proxyLocalVideoSink
    }

    override fun getRemoteRenderer(): ProxyVideoSink {
        return mediaSourceDataSource.remoteLocalVideoSink
    }

    fun getRemoteVideoTrack(peerConnection: PeerConnection): VideoTrack {
        return peerConnection.transceivers.filter { it.mediaType == MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO }.first().receiver.track() as VideoTrack
    }
}