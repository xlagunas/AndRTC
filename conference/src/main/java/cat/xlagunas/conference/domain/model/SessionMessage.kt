package cat.xlagunas.conference.domain.model

import org.webrtc.SessionDescription

data class SessionMessage(
    val sessionDescription: SessionDescription,
    val receiver: String
)