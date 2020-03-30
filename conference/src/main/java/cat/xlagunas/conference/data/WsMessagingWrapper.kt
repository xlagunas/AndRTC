package cat.xlagunas.conference.data

import cat.xlagunas.conference.data.dto.ConnectedUser
import cat.xlagunas.conference.data.dto.MessageDto
import cat.xlagunas.conference.domain.model.IceCandidateMessage
import cat.xlagunas.conference.domain.model.MessageType
import cat.xlagunas.conference.domain.model.SessionMessage
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

class WsMessagingWrapper @Inject constructor(private val socket: Socket, private val gson: Gson) {

    val getNewJoiners = BroadcastChannel<ConnectedUser>(100)
    val observeSessionStream = Channel<Pair<SessionMessage, MessageType>>()
    val observeIceCandidateStream = Channel<IceCandidateMessage>()

    fun setupWebSocketListeners() {
        onNewJoiner()
        onNewMessage()
    }

    fun sendMessage(messageType: String, message: MessageDto) {
        socket.emit(messageType, gson.toJson(message))
    }

    private fun onNewMessage() {
        socket.on("DIRECT_MESSAGE") { content ->
            GlobalScope.launch {

                val message = content
                        .map { it as String }
                        .map { data -> gson.fromJson(data, MessageDto::class.java) }.first()

                when (message.type) {
                    MessageType.OFFER -> {
                        val session = gson.fromJson(message.data, SessionMessage::class.java)
                        observeSessionStream.send(Pair(session, message.type))
                    }
                    MessageType.ANSWER -> {
                        val session = gson.fromJson(message.data, SessionMessage::class.java)
                        observeSessionStream.send(Pair(session, message.type))
                    }
                    MessageType.ICE_CANDIDATE -> {
                        val iceCandidate = gson.fromJson(message.data, IceCandidateMessage::class.java)
                        observeIceCandidateStream.send(iceCandidate)
                    }
                }
            }
        }
    }

    private fun onNewJoiner() {
        socket.on("NEW_USER") { user ->
            val attendingUsers = user.map { it as String }.map { userId -> ConnectedUser(userId) }.first()
            GlobalScope.launch { getNewJoiners.send(attendingUsers) }
        }
    }

}