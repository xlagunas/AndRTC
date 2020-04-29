package cat.xlagunas.ws_messaging

import androidx.lifecycle.LifecycleObserver
import cat.xlagunas.ws_messaging.data.MessageDto
import cat.xlagunas.ws_messaging.data.UserSession
import cat.xlagunas.ws_messaging.model.AnswerMessage
import cat.xlagunas.ws_messaging.model.IceCandidateMessage
import cat.xlagunas.ws_messaging.model.Message
import cat.xlagunas.ws_messaging.model.MessageMapper
import cat.xlagunas.ws_messaging.model.MessageType
import cat.xlagunas.ws_messaging.model.OfferMessage
import cat.xlagunas.ws_messaging.model.Session
import cat.xlagunas.ws_messaging.model.SessionMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WebSocketController @Inject constructor(
    private val messageMapper: MessageMapper,
    private val sessionMapper: SessionMapper,
    private val webSocketProvider: WebSocketEmitterProvider
) : LifecycleObserver {

    private val localSession by lazy { UserSession(webSocketProvider.getId()) }

    fun joinConference(conferenceId: String) {
        webSocketProvider.getEmitter().emit("JOIN_ROOM", conferenceId)
    }

    fun sendMessage(message: Message) {
        val messageDto = when (message) {
            is OfferMessage -> MessageDto(
                localSession.getId(),
                message.receiver.getId(),
                MessageType.OFFER,
                messageMapper.serializeMessage(message.copy(receiver = localSession))
            )
            is AnswerMessage -> MessageDto(
                localSession.getId(),
                message.receiver.getId(),
                MessageType.ANSWER,
                messageMapper.serializeMessage(message.copy(receiver = localSession))
            )
            is IceCandidateMessage -> MessageDto(
                localSession.getId(),
                message.receiver.getId(),
                MessageType.ICE_CANDIDATE,
                messageMapper.serializeMessage(message.copy(receiver = localSession))
            )
        }
        webSocketProvider.getEmitter()
            .emit("DIRECT_MESSAGE", messageMapper.serializeMessageDto(messageDto))
    }

    fun observeDirectMessages(): Flow<Message> {
        return webSocketProvider.getEmitter().on("DIRECT_MESSAGE")
            .map { messageMapper.convertMessageDto(it) }
    }

    fun observeParticipants(): Flow<Session> {
        return webSocketProvider.getEmitter().on("NEW_USER")
            .map { sessionMapper.convertSession(it) }
    }
}