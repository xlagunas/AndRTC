package cat.xlagunas.conference.data

import org.webrtc.PeerConnection

data class PeerData(val peerConnection: PeerConnection, val sdpObserver: VivSdpObserver)