package cat.xlagunas.conference.data

import cat.xlagunas.conference.data.dto.MessageDto
import cat.xlagunas.conference.data.dto.RoomDetailsDto
import cat.xlagunas.conference.domain.model.IceCandidateMessage
import cat.xlagunas.conference.domain.model.MessageType
import cat.xlagunas.conference.domain.model.SessionMessage
import com.google.gson.Gson
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class WsMessagingWrapper @Inject constructor(private val wsMessagingApi: WsMessagingApi, private val gson: Gson) {

    val getRoomParticipants = BroadcastChannel<RoomDetailsDto>(1)
    val observeSessionStream = Channel<Pair<SessionMessage, MessageType>>()
    val observeIceCandidateStream = Channel<IceCandidateMessage>()

    fun setupWebSocketListeners() {
        GlobalScope.launch {
            wsMessagingApi.getMessage().consumeEach {
                when (it.type) {
                    MessageType.OFFER -> {
                        val session = gson.fromJson(it.data, SessionMessage::class.java)
                        observeSessionStream.send(Pair(session, it.type))
                    }
                    MessageType.ANSWER -> {
                        val session = gson.fromJson(it.data, SessionMessage::class.java)
                        observeSessionStream.send(Pair(session, it.type))
                    }
                    MessageType.ICE_CANDIDATE -> {
                        val iceCandidate = gson.fromJson(it.data, IceCandidateMessage::class.java)
                        observeIceCandidateStream.send(iceCandidate)
                    }
                    MessageType.ROOM_DISCOVERY -> {
                        val roomDetails = gson.fromJson(it.data, RoomDetailsDto::class.java)
                        getRoomParticipants.send(roomDetails)
                    }
                }
            }
        }
    }

    fun observeMessageEvent(): ReceiveChannel<WebSocket.Event> {
        return wsMessagingApi.observeMessageEvent()
    }

    fun sendMessage(message: MessageDto) {
        return wsMessagingApi.sendMessage(message)
    }
}