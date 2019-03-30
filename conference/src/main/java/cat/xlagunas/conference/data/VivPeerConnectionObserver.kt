package cat.xlagunas.conference.data

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import timber.log.Timber

class VivPeerConnectionObserver(private val userId: String, private val handler: WebRTCEventHandler) :
    PeerConnection.Observer {

    override fun onIceCandidate(p0: IceCandidate) {
        Timber.i("New ice candidate gathered for $userId")
        GlobalScope.launch { handler.iceCandidateHandler.send(Pair(this@VivPeerConnectionObserver.userId, p0)) }
    }

    override fun onDataChannel(p0: DataChannel) {
        Timber.i("incoming data from dataChannel: $p0 from $userId")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Timber.i("onIceConnectionReceivingChange for user $userId to state $p0")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState) {
        Timber.i("onIceConnectionChange for user $userId to state ${p0.name}")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState) {
        Timber.i("onIceGatheringChange for user $userId to state ${p0.name}")
    }

    override fun onAddStream(p0: MediaStream) {
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState) {
        Timber.i("Signaling change for $userId: ${p0.name}")
        GlobalScope.launch { handler.signalingStateHandler.send(Pair(this@VivPeerConnectionObserver.userId, p0)) }
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>) {
        Timber.i("Ice candidate removed for customer $userId")
    }

    override fun onRemoveStream(p0: MediaStream) {
    }

    override fun onRenegotiationNeeded() {
    }

    override fun onAddTrack(p0: RtpReceiver, p1: Array<out MediaStream>) {
        Timber.i("received onAddTrack message")
    }

    enum class MediaStreamState {
        ADDED, REMOVED
    }
}