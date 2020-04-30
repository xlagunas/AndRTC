package cat.xlagunas.signaling

import cat.xlagunas.signaling.data.UserSession
import cat.xlagunas.signaling.domain.AnswerMessage
import cat.xlagunas.signaling.domain.IceCandidateMessage
import cat.xlagunas.signaling.domain.OfferMessage
import cat.xlagunas.signaling.domain.Session
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

object TestUtils{
    fun fakeLocalSession(): Session =
        UserSession("1234")
    fun fakeOfferSessionDescription() =
        SessionDescription(
            SessionDescription.Type.OFFER,
            "fake offer description"
        )
    fun fakeAnswerSessionDescription() =
        SessionDescription(
            SessionDescription.Type.ANSWER,
            "fake answer description"
        )
    fun fakeIceCandidate() =
        IceCandidate(
            "fakeSdpMid",
            1024,
            "fakeSdp for ice candidate string"
        )

    fun fakeIceCandidateMessage() =
        IceCandidateMessage(
            fakeLocalSession(),
            fakeIceCandidate()
        )
    fun fakeAnswerSessionMessage() = AnswerMessage(
        fakeLocalSession(),
        fakeAnswerSessionDescription()
    )
    fun fakeOfferSessionMessage() = OfferMessage(
        fakeLocalSession(),
        fakeOfferSessionDescription()
    )
}