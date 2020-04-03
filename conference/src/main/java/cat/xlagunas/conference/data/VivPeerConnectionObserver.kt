package cat.xlagunas.conference.data

import cat.xlagunas.ws_messaging.model.Session
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import timber.log.Timber

class VivPeerConnectionObserver(
    private val user: Session,
    private val onIceCandidate: (iceCandidate: Pair<Session, IceCandidate>) -> Unit
) :
    PeerConnection.Observer {

    override fun onIceCandidate(p0: IceCandidate) {
        Timber.i("New ice candidate gathered for ${user.getId()}")
        onIceCandidate.invoke(Pair(user, p0))
    }

    override fun onDataChannel(p0: DataChannel) {
        Timber.i("incoming data from dataChannel: $p0 from ${user.getId()}")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Timber.i("onIceConnectionReceivingChange for user ${user.getId()} to state $p0")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState) {
        Timber.i("onIceConnectionChange for user ${user.getId()} to state ${p0.name}")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState) {
        Timber.i("onIceGatheringChange for user ${user.getId()} to state ${p0.name}")
    }

    override fun onAddStream(p0: MediaStream) {
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState) {
        Timber.i("Signaling change for ${user.getId()}: ${p0.name}")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>) {
        Timber.i("Ice candidate removed for customer ${user.getId()}")
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