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
import kotlinx.coroutines.channels.BroadcastChannel
import javax.inject.Inject

class WebSocketController @Inject constructor(
    private val messageMapper: MessageMapper,
    private val sessionMapper: SessionMapper,
    private val webSocketProvider: WebSocketEmitterProvider
) : LifecycleObserver {

    private val localSession by lazy { UserSession(webSocketProvider.getId()) }
    val receivedMessageChannel = BroadcastChannel<Message>(100)
    val participantsChannel: BroadcastChannel<Session> = BroadcastChannel(10)

    fun joinConference(conferenceId: String) {
        setDirectMessageListener()
        setParticipantsListener()
        webSocketProvider.getEmitter().emit("JOIN_ROOM", conferenceId)
    }

    fun sendMessage(message: Message) {
        val messageDto = when (message) {
            is OfferMessage -> MessageDto(
                localSession.getId(),
                message.receiver.getId(),
                MessageType.OFFER,
                messageMapper.serializeMessage(message)
            )
            is AnswerMessage -> MessageDto(
                localSession.getId(),
                message.receiver.getId(),
                MessageType.ANSWER,
                messageMapper.serializeMessage(message)
            )
            is IceCandidateMessage -> MessageDto(
                localSession.getId(),
                message.receiver.getId(),
                MessageType.ICE_CANDIDATE,
                messageMapper.serializeMessage(message)
            )
        }
        webSocketProvider.getEmitter().emit("DIRECT_MESSAGE", messageDto)
    }

    private fun setDirectMessageListener() {
        webSocketProvider.getEmitter().on("DIRECT_MESSAGE") { receivedMsg ->
            receivedMsg.map { messageMapper.convertMessage(it) }
                .forEach { message -> receivedMessageChannel.offer(message) }
        }
    }

    private fun setParticipantsListener() {
        webSocketProvider.getEmitter().on("NEW_USER") { message ->
            message.map { it as String }
                .map { userId -> sessionMapper.convertSession(userId) }
                .forEach { participantsChannel.offer(it) }
        }
    }
}