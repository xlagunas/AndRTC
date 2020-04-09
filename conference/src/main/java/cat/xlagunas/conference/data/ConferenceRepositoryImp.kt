package cat.xlagunas.conference.data

import cat.xlagunas.conference.data.MediaDataSourceImp.Companion.VIDEO_TRACK_ID
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.PeerConnectionDataSource
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import cat.xlagunas.ws_messaging.WsSignalingProtocol
import cat.xlagunas.ws_messaging.model.Session
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    private val peerConnectionDataSource: PeerConnectionDataSource,
    private val mediaSourceDataSource: MediaDataSourceImp,
    private val signaling: WsSignalingProtocol
) : ConferenceRepository {

    override fun joinRoom(roomId: String) {
        signaling.joinConference(roomId)
    }

    override suspend fun onNewUser(): Flow<Session> {
        return signaling.onNewSession()
    }

    override suspend fun registerUser() {
    }

    override fun logoutRoom() {
        Timber.d("Logging out room!")
    }

    override fun addIceCandidate(contact: Session, iceCandidate: IceCandidate) {
        Timber.d("Adding Ice candidate for ${contact.getId()}")
        peerConnectionDataSource.getPeerConnection(contact.getId())?.addIceCandidate(iceCandidate)
    }

    override fun createPeerConnection(user: Session, onIceCandidateGenerated: (iceCandidate: Pair<Session, IceCandidate>) -> Unit): PeerConnection? {
        val peer = peerConnectionDataSource.createPeerConnection(user, onIceCandidateGenerated)
        val videoCapturer =
            mediaSourceDataSource.createLocalVideoCapturer(mediaSourceDataSource.getCameraEnumerator())
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

    override fun createOffer(
        user: Session,
        mediaConstraints: MediaConstraints
    ): Flow<Pair<Session, SessionDescription>> = callbackFlow {
        peerConnectionDataSource.createOffer(
            user.getId(),
            mediaConstraints
        ) { sessionDescription ->
            launch { send(Pair(user, sessionDescription)) }
        }
        awaitClose()
    }

    override fun handleRemoteOffer(
        contact: Session,
        mediaConstraints: MediaConstraints,
        sessionDescription: SessionDescription
    ): Flow<Pair<Session, SessionDescription>> {
        return callbackFlow {
            Timber.d("Setting up remote offer to peer connection")
            peerConnectionDataSource.handleRemoteOffer(contact.getId(), sessionDescription, mediaConstraints) {
                    answer -> launch {  offer(Pair(contact, answer))}
            }
            awaitClose()
        }
    }

    override fun handleRemoteAnswer(
        contact: Session,
        sessionDescription: SessionDescription,
        onComplete: () -> Unit
    ) {
        Timber.d("Setting up remote answer to peer connection")
        peerConnectionDataSource.handleRemoteAnswer(contact.getId(), sessionDescription) {
            onComplete.invoke()
        }
    }

    override fun getLocalRenderer(): ProxyVideoSink {
        return mediaSourceDataSource.proxyLocalVideoSink
    }

    override fun getRemoteRenderer(): ProxyVideoSink {
        return mediaSourceDataSource.remoteLocalVideoSink
    }

    fun getRemoteVideoTrack(peerConnection: PeerConnection): VideoTrack {
        return peerConnection.transceivers.filter { it.mediaType == MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO }
            .first().receiver.track() as VideoTrack
    }
}