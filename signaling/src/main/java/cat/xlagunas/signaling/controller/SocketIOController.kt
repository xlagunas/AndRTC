package cat.xlagunas.signaling.controller

import androidx.lifecycle.LifecycleObserver
import cat.xlagunas.signaling.data.MessageDto
import cat.xlagunas.signaling.data.SocketIOEmitterProvider
import cat.xlagunas.signaling.data.UserSession
import cat.xlagunas.signaling.data.mapper.MessageMapper
import cat.xlagunas.signaling.data.mapper.SessionMapper
import cat.xlagunas.signaling.domain.AnswerMessage
import cat.xlagunas.signaling.domain.IceCandidateMessage
import cat.xlagunas.signaling.domain.Message
import cat.xlagunas.signaling.domain.MessageType
import cat.xlagunas.signaling.domain.OfferMessage
import cat.xlagunas.signaling.domain.Session
import cat.xlagunas.signaling.data.extensions.on
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SocketIOController @Inject constructor(
    private val messageMapper: MessageMapper,
    private val sessionMapper: SessionMapper,
    private val socketIOProvider: SocketIOEmitterProvider
) : LifecycleObserver {

    private val localSession by lazy { UserSession(socketIOProvider.getId()) }

    fun joinConference(conferenceId: String) {
        socketIOProvider.getEmitter().emit("JOIN_ROOM", conferenceId)
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
        socketIOProvider.getEmitter()
            .emit("DIRECT_MESSAGE", messageMapper.serializeMessageDto(messageDto))
    }

    fun observeDirectMessages(): Flow<Message> {
        return socketIOProvider.getEmitter().on("DIRECT_MESSAGE")
            .map { messageMapper.convertMessageDto(it) }
    }

    fun observeParticipants(): Flow<Session> {
        return socketIOProvider.getEmitter().on("NEW_USER")
            .map { sessionMapper.convertSession(it) }
    }
}