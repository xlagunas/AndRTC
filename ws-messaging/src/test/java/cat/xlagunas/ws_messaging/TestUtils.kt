package cat.xlagunas.ws_messaging

import cat.xlagunas.ws_messaging.data.UserSession
import cat.xlagunas.ws_messaging.model.AnswerMessage
import cat.xlagunas.ws_messaging.model.IceCandidateMessage
import cat.xlagunas.ws_messaging.model.OfferMessage
import cat.xlagunas.ws_messaging.model.Session
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

    fun fakeIceCandidateMessage() = IceCandidateMessage(fakeLocalSession(), fakeIceCandidate())
    fun fakeAnswerSessionMessage() = AnswerMessage(fakeLocalSession(), fakeAnswerSessionDescription())
    fun fakeOfferSessionMessage() = OfferMessage(fakeLocalSession(), fakeOfferSessionDescription())
}