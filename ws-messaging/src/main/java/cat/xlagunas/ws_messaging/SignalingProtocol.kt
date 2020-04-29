package cat.xlagunas.ws_messaging

import cat.xlagunas.ws_messaging.model.AnswerMessage
import cat.xlagunas.ws_messaging.model.IceCandidateMessage
import cat.xlagunas.ws_messaging.model.OfferMessage
import cat.xlagunas.ws_messaging.model.Session
import kotlinx.coroutines.flow.Flow

interface SignalingProtocol {
    fun joinConference(conferenceId: String)
    fun onNewSession(): Flow<Session>

    fun sendOffer(offer: OfferMessage)
    fun sendAnswer(answer: AnswerMessage)
    fun sendIceCandidate(iceCandidate: IceCandidateMessage)

    fun onReceiveOffer(): Flow<OfferMessage>
    fun onReceiveAnswer(): Flow<AnswerMessage>
    fun onReceiveIceCandidate(): Flow<IceCandidateMessage>
}