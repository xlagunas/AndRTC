package cat.xlagunas.conference.domain

import cat.xlagunas.conference.data.MessageDto
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.channels.ReceiveChannel

interface WsMessagingApi {

    @Receive
    fun observeMessageEvent(): ReceiveChannel<WebSocket.Event>

    @Receive
    fun observeSessionStream(): ReceiveChannel<SessionMessage>

    @Receive
    fun observeIceCandidateStream(): ReceiveChannel<IceCandidateMessage>

    @Receive
    fun getRoomParticipants(): ReceiveChannel<List<RoomParticipant>>

    @Send
    fun sendMessage(messageDto: MessageDto)

    @Send
    fun sendMessage(sessionMessage: SessionMessage)

    @Send
    fun sendMessage(iceCandidateMessage: IceCandidateMessage)
}