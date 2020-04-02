package cat.xlagunas.ws_messaging.model

import cat.xlagunas.ws_messaging.data.MessageDto
import cat.xlagunas.ws_messaging.data.UserSession
import com.google.gson.Gson
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import javax.inject.Inject

class MessageMapper @Inject constructor(private val gson: Gson) {

    fun convertMessage(jsonMessage: String): Message {
        val messageDto = gson.fromJson(jsonMessage, MessageDto::class.java)
        return when (messageDto.type) {
            MessageType.OFFER -> OfferMessage(
                UserSession(messageDto.localId),
                gson.fromJson(messageDto.data, SessionDescription::class.java)
            )
            MessageType.ANSWER -> AnswerMessage(
                UserSession(messageDto.localId),
                gson.fromJson(messageDto.data, SessionDescription::class.java)
            )
            MessageType.ICE_CANDIDATE -> IceCandidateMessage(
                UserSession(messageDto.localId),
                gson.fromJson(messageDto.data, IceCandidate::class.java)
            )
        }
    }

    fun serializeMessage(message: Message): String {
        return gson.toJson(message)
    }
}