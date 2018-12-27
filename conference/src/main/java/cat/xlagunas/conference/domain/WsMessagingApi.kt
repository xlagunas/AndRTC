package cat.xlagunas.conference.domain

import cat.xlagunas.conference.data.dto.MessageDto
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.channels.ReceiveChannel

interface WsMessagingApi {

    @Receive
    fun observeMessageEvent(): ReceiveChannel<WebSocket.Event>

    @Receive fun getMessage(): ReceiveChannel<MessageDto>

    @Send
    fun sendMessage(messageDto: MessageDto)
}