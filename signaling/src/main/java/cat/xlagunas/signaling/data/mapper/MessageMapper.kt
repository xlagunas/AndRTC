package cat.xlagunas.signaling.data.mapper

import cat.xlagunas.signaling.data.MessageDto
import cat.xlagunas.signaling.domain.AnswerMessage
import cat.xlagunas.signaling.domain.IceCandidateMessage
import cat.xlagunas.signaling.domain.Message
import cat.xlagunas.signaling.domain.MessageType
import cat.xlagunas.signaling.domain.OfferMessage
import com.google.gson.Gson
import javax.inject.Inject

class MessageMapper @Inject constructor(private val gson: Gson) {

    private fun convertMessage(messageDto: MessageDto): Message {
        return when (messageDto.type) {
            MessageType.OFFER -> gson.fromJson(messageDto.data, OfferMessage::class.java)
            MessageType.ANSWER -> gson.fromJson(messageDto.data, AnswerMessage::class.java)
            MessageType.ICE_CANDIDATE -> gson.fromJson(messageDto.data, IceCandidateMessage::class.java)
        }
    }

    fun serializeMessage(message: Message): String {
        return gson.toJson(message)
    }

    fun convertMessageDto(serializedMessage: String): Message {
        val messageDto = gson.fromJson(serializedMessage, MessageDto::class.java)
        return convertMessage(messageDto)
    }

    fun serializeMessageDto(messageDto: MessageDto): String = gson.toJson(messageDto)
}
