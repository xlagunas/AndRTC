package cat.xlagunas.signaling

import cat.xlagunas.signaling.controller.SocketIOController
import cat.xlagunas.signaling.domain.AnswerMessage
import cat.xlagunas.signaling.domain.IceCandidateMessage
import cat.xlagunas.signaling.domain.Message
import cat.xlagunas.signaling.domain.OfferMessage
import cat.xlagunas.signaling.domain.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class SocketIOSignaling @Inject constructor(
    private val socketIOController: SocketIOController
) : Signaling {

    override fun joinConference(conferenceId: String) {
        socketIOController.joinConference(conferenceId)
    }

    override fun onNewSession(): Flow<Session> {
        return socketIOController.observeParticipants()
    }

    override fun sendOffer(offer: OfferMessage) {
        Timber.d("Sending offer message")
        socketIOController.sendMessage(offer)
    }

    override fun sendAnswer(answer: AnswerMessage) {
        Timber.d("Sending answer message")
        socketIOController.sendMessage(answer)
    }

    override fun sendIceCandidate(iceCandidate: IceCandidateMessage) {
        Timber.d("Sending iceCandidate message")
        socketIOController.sendMessage(iceCandidate)
    }

    override fun onReceiveOffer(): Flow<OfferMessage> {
        return socketIOController.observeDirectMessages()
            .filter { it is OfferMessage }
            .map { value: Message -> value as OfferMessage }
    }

    override fun onReceiveAnswer(): Flow<AnswerMessage> {
        return socketIOController.observeDirectMessages()
            .filter { it is AnswerMessage }
            .map { value: Message -> value as AnswerMessage }
    }

    override fun onReceiveIceCandidate(): Flow<IceCandidateMessage> {
        return socketIOController.observeDirectMessages()
            .filter { it is IceCandidateMessage }
            .map { value: Message -> value as IceCandidateMessage }
    }
}