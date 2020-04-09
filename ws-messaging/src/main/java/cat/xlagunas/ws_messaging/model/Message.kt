package cat.xlagunas.ws_messaging.model

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

sealed class Message {
    abstract val receiver: Session
}

data class OfferMessage(
    override val receiver: Session,
    val offer: SessionDescription
) : Message()

data class AnswerMessage(
    override val receiver: Session,
    val answer: SessionDescription
) : Message()

data class IceCandidateMessage(
    override val receiver: Session,
    val iceCandidate: IceCandidate
) : Message()