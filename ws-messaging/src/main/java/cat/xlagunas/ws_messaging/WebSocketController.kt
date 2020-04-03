package cat.xlagunas.ws_messaging

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import cat.xlagunas.ws_messaging.data.MessageDto
import cat.xlagunas.ws_messaging.data.UserSession
import cat.xlagunas.ws_messaging.model.AnswerMessage
import cat.xlagunas.ws_messaging.model.IceCandidateMessage
import cat.xlagunas.ws_messaging.model.Message
import cat.xlagunas.ws_messaging.model.MessageMapper
import cat.xlagunas.ws_messaging.model.MessageType
import cat.xlagunas.ws_messaging.model.OfferMessage
import cat.xlagunas.ws_messaging.model.Session
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.channels.BroadcastChannel
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject

class WebSocketController @Inject constructor(
    activity: AppCompatActivity,
    okHttpClient: OkHttpClient,
    private val messageMapper: MessageMapper,
    private val roomId: String
) : LifecycleObserver {

    private val socket: Socket by lazy { IO.socket("https://wss.viv.cat") }
    private val localSession: UserSession by lazy { UserSession(socket.id()) }

    val receivedMessageChannel = BroadcastChannel<Message>(100)
    val participantsChannel: BroadcastChannel<Session> = BroadcastChannel(10)

    init {
        Timber.d("Binding Socket.IO lifecycle to activity")
        activity.lifecycle.addObserver(this)
        IO.setDefaultOkHttpCallFactory(okHttpClient)
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
        socket.emit("DIRECT_MESSAGE", messageDto)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        if (!socket.connected()) {
            socket.connect()
            setDirectMessageListener()
            setParticipantsListener()
            socket.emit("JOIN_ROOM", roomId)
            Timber.d("Connecting Socket.IO instance")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        socket.disconnect()
        Timber.d("Disconnecting Socket.IO instance")
    }

    private fun setDirectMessageListener() {
        socket.on("DIRECT_MESSAGE") { receivedMsg ->
            receivedMsg.map { it as String }
                .map { messageMapper.convertMessage(it) }
                .forEach { message -> receivedMessageChannel.offer(message) }
        }
    }

    private fun setParticipantsListener() {
        socket.on("NEW_USER") { message ->
            message.map { it as String }
                .map { userId -> UserSession(userId) }
                .forEach { participantsChannel.offer(it) }
        }
    }
}