package cat.xlagunas.conference.data

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import timber.log.Timber
import javax.inject.Inject

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
        Timber.i("onIceConnectionReceivingChange for user $userId to state ${p0}")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState) {
        Timber.i("onIceConnectionChange for user $userId to state ${p0.name}")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState) {
        Timber.i("onIceGatheringChange for user $userId to state ${p0.name}")
    }

    override fun onAddStream(p0: MediaStream) {
        Timber.i("On Added stream called for $userId")
        GlobalScope.launch {
            handler.mediaStreamChannel.send(
                Triple(
                    this@VivPeerConnectionObserver.userId,
                    p0,
                    MediaStreamState.ADDED
                )
            )
        }
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState) {
        Timber.i("Signaling change for $userId: ${p0.name}")
        GlobalScope.launch { handler.signalingStateHandler.send(Pair(this@VivPeerConnectionObserver.userId, p0)) }
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>) {
        Timber.i("Ice candidate removed for customer $userId")
    }

    override fun onRemoveStream(p0: MediaStream) {
        Timber.i("On Remove stream called for $userId")
        GlobalScope.launch {
            handler.mediaStreamChannel.send(
                Triple(
                    this@VivPeerConnectionObserver.userId,
                    p0,
                    MediaStreamState.REMOVED
                )
            )
        }
    }

    override fun onRenegotiationNeeded() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddTrack(p0: RtpReceiver, p1: Array<out MediaStream>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class WebRTCEventHandler @Inject constructor() {
        val mediaStreamChannel = Channel<Triple<String, MediaStream, MediaStreamState>>()
        val iceCandidateHandler = Channel<Pair<String, IceCandidate>>()
        val signalingStateHandler = Channel<Pair<String, PeerConnection.SignalingState>>()
        val sessionDescriptionHandler = Channel<Triple<String, SessionDescription, SessionDescriptionState>>()
    }

    enum class MediaStreamState {
        ADDED, REMOVED
    }

    enum class SessionDescriptionState {
        OFFER, ANSWER
    }
}