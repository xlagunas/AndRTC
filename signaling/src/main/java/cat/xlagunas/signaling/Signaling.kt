package cat.xlagunas.signaling

import cat.xlagunas.signaling.domain.AnswerMessage
import cat.xlagunas.signaling.domain.IceCandidateMessage
import cat.xlagunas.signaling.domain.OfferMessage
import cat.xlagunas.signaling.domain.Session
import kotlinx.coroutines.flow.Flow

interface Signaling {
    fun joinConference(conferenceId: String)
    fun onNewSession(): Flow<Session>

    fun sendOffer(offer: OfferMessage)
    fun sendAnswer(answer: AnswerMessage)
    fun sendIceCandidate(iceCandidate: IceCandidateMessage)

    fun onReceiveOffer(): Flow<OfferMessage>
    fun onReceiveAnswer(): Flow<AnswerMessage>
    fun onReceiveIceCandidate(): Flow<IceCandidateMessage>
}
