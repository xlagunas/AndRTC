package cat.xlagunas.conference.domain.model

import org.webrtc.IceCandidate

data class IceCandidateMessage(
    val iceCandidate: IceCandidate,
    val receiver: String
)