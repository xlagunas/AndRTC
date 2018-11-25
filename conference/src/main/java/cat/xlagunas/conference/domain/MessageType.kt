package cat.xlagunas.conference.domain

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

data class SessionMessage(
    val sessionDescription: SessionDescription,
    val receiver: String
)

data class IceCandidateMessage(
    val iceCandidate: IceCandidate,
    val receiver: String
)