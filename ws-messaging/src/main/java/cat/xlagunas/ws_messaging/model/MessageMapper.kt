package cat.xlagunas.ws_messaging.model

import cat.xlagunas.ws_messaging.data.MessageDto
import cat.xlagunas.ws_messaging.data.UserSession
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import javax.inject.Inject

class MessageMapper @Inject constructor(private val gson: Gson) {

    fun convertMessage(serializedMessage: Any): Message {

        val messageDto =
            if (serializedMessage is MessageDto) serializedMessage
            else {
                val stringMessage = serializedMessage as String
                gson.fromJson(stringMessage, MessageDto::class.java)
            }
        return when (messageDto.type) {
            MessageType.OFFER -> gson.fromJson(messageDto.data, OfferMessage::class.java)
            MessageType.ANSWER -> gson.fromJson(messageDto.data, AnswerMessage::class.java)
            MessageType.ICE_CANDIDATE -> gson.fromJson(
                messageDto.data,
                IceCandidateMessage::class.java
            )
        }
    }

    fun serializeMessage(message: Message): String {
        return gson.toJson(message)
    }
}