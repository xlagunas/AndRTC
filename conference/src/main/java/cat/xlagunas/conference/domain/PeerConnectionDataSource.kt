package cat.xlagunas.conference.domain

import cat.xlagunas.conference.data.NoOPVivSdpObserver
import cat.xlagunas.conference.data.VivPeerConnectionObserver
import cat.xlagunas.conference.data.VivSdpObserver
import cat.xlagunas.signaling.domain.Session
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import org.webrtc.IceCandidate
import org.webrtc.Logging
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import timber.log.Timber

class PeerConnectionDataSource @Inject constructor(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val rtcConfiguration: PeerConnection.RTCConfiguration
) {
    private val peerConnectionMap = ConcurrentHashMap<String, PeerConnection>()

    fun getPeerConnection(userId: String): PeerConnection? {
        return peerConnectionMap[userId]
    }

    private fun addPeerConnection(userId: String, peerConnection: PeerConnection) {
        peerConnectionMap[userId] = peerConnection
    }

    fun createPeerConnection(user: Session, onIceCandidate: (iceCandidate: Pair<Session, IceCandidate>) -> Unit): PeerConnection? {
        Timber.d("Creating peer connection per ${user.getId()}")
        val peerObserver = VivPeerConnectionObserver(user, onIceCandidate)
        return peerConnectionFactory.createPeerConnection(rtcConfiguration, peerObserver)?.apply {
            Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)
            addPeerConnection(user.getId(), this)
        }
    }

    fun createOffer(
        userId: String,
        peerConnectionConstraints: MediaConstraints,
        block: (sessionDescription: SessionDescription) -> Unit
    ) {
        Timber.d("Creating offer for $userId")
        val sdpObserver = object : VivSdpObserver(userId) {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                getPeerConnection(userId)?.setLocalDescription(this, sessionDescription)
                block(sessionDescription)
            }
        }
        getPeerConnection(userId)?.createOffer(sdpObserver, peerConnectionConstraints)
    }

    fun handleRemoteAnswer(
        contactId: String,
        sessionDescription: SessionDescription,
        block: (() -> Unit)
    ) {
        Timber.d("Handling remote answer for $contactId")
        val peerConnection = getPeerConnection(contactId)
        val sdpObserver = object : VivSdpObserver(contactId) {
            override fun onSetSuccess() {
                super.onSetSuccess()
                block()
            }
        }
        peerConnection?.setRemoteDescription(sdpObserver, sessionDescription)
    }

    fun handleRemoteOffer(
        contactId: String,
        sessionDescription: SessionDescription,
        constraints: MediaConstraints,
        block: (sessionDescription: SessionDescription) -> Unit
    ) {

        Timber.d("Handling remote offer from $contactId")
        val peerConnection = getPeerConnection(contactId)
        peerConnection?.setRemoteDescription(NoOPVivSdpObserver(contactId), sessionDescription)

        val sdpObserver = object : VivSdpObserver(contactId) {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                Timber.d("Sending answer message to $contactId")
                peerConnection?.setLocalDescription(this, sessionDescription)
                block(sessionDescription)
            }
        }
        peerConnection?.createAnswer(sdpObserver, constraints)
    }
}
