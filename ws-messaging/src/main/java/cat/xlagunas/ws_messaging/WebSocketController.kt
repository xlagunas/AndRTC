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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import timber.log.Timber
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
        webSocketProvider.getEmitter().emit("DIRECT_MESSAGE", messageMapper.serializeMessageDto(messageDto))
    }

    private fun setDirectMessageListener() {
       webSocketProvider.getEmitter().on("DIRECT_MESSAGE") { receivedMsg ->
            receivedMsg.map { it as String }
                .map { messageMapper.convertMessageDto(it) }
                .forEach { message ->
                    Timber.v("received direct message of type ${message.javaClass.canonicalName}")
                    GlobalScope.launch { receivedMessageChannel.send(message) }
                }
        }
    }

    private fun setParticipantsListener() {
        webSocketProvider.getEmitter().on("NEW_USER") { message ->
            message.map { it as String }
                .map { userId -> sessionMapper.convertSession(userId) }
                .forEach {
                    GlobalScope.launch { participantsChannel.send(it) }
                }
        }
    }
}