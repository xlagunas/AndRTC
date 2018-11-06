package cat.xlagunas.conference.domain

import cat.xlagunas.conference.data.Message
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.channels.ReceiveChannel

interface WsMessagingApi {

    @Receive
    fun observeMessageStream(): ReceiveChannel<Message>

    @Receive
    fun getRoomParticipants(): List<RoomParticipant>

    @Send
    fun sendMessage(message: Message)
}