package cat.xlagunas.conference.data

import kotlinx.coroutines.channels.Channel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import javax.inject.Inject

class WebRTCEventHandler @Inject constructor() {
    val mediaStreamChannel =
        Channel<Triple<String, MediaStream, VivPeerConnectionObserver.MediaStreamState>>()
    val iceCandidateHandler = Channel<Pair<String, IceCandidate>>()
    val signalingStateHandler =
        Channel<Pair<String, PeerConnection.SignalingState>>()
}