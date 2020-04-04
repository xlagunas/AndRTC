package cat.xlagunas.ws_messaging

import cat.xlagunas.ws_messaging.model.AnswerMessage
import cat.xlagunas.ws_messaging.model.IceCandidateMessage
import cat.xlagunas.ws_messaging.model.Message
import cat.xlagunas.ws_messaging.model.OfferMessage
import cat.xlagunas.ws_messaging.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WsSignalingProtocol @Inject constructor(
    private val webSocketController: WebSocketController
) : SignalingProtocol {

    override fun joinConference(conferenceId: String) {
        webSocketController.joinConference(conferenceId)
    }

    override fun onNewSession(): Flow<Session> {
        return webSocketController.participantsChannel.asFlow()
    }

    override fun sendOffer(offer: OfferMessage) {
        webSocketController.sendMessage(offer)
    }

    override fun sendAnswer(answer: AnswerMessage) {
        webSocketController.sendMessage(answer)
    }

    override fun sendIceCandidate(iceCandidate: IceCandidateMessage) {
        webSocketController.sendMessage(iceCandidate)
    }

    override fun onReceiveOffer(): Flow<OfferMessage> {
        return webSocketController.receivedMessageChannel.asFlow()
            .filter { it is OfferMessage }
            .map { value: Message -> value as OfferMessage }
    }

    override fun onReceiveAnswer(): Flow<AnswerMessage> {
        return webSocketController.receivedMessageChannel.asFlow()
            .filter { it is AnswerMessage }
            .map { value: Message -> value as AnswerMessage }
    }

    override fun onReceiveIceCandidate(): Flow<IceCandidateMessage> {
        return webSocketController.receivedMessageChannel.asFlow()
            .filter { it is IceCandidateMessage }
            .map { value: Message -> value as IceCandidateMessage }
    }
}