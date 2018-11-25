package cat.xlagunas.conference.data

import cat.xlagunas.conference.domain.IceCandidateMessage
import cat.xlagunas.conference.domain.SessionMessage
import com.google.gson.Gson
import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import java.lang.reflect.Type
import javax.inject.Inject

class WebRtcMessageAdapter private constructor(
    private val userId: String,
    private val gson: Gson,
    private val gsonMessageAdapter: MessageAdapter<*>
) : MessageAdapter<Any> {

    override fun toMessage(data: Any): Message {
        val messageWithFrom = (data as MessageDto).copy(from = userId)
        return Message.Text(gson.toJson(messageWithFrom))
    }

    override fun fromMessage(message: Message): Any {
        val messageDto = gsonMessageAdapter.fromMessage(message) as MessageDto

        return when (messageDto.type) {
            MessageType.ICE_CANDIDATE -> gson.fromJson(messageDto.data, IceCandidateMessage::class.java)
            MessageType.SESSION_DESCRIPTION -> gson.fromJson(messageDto.data, SessionMessage::class.java)
            MessageType.SERVER -> {
                throw UnsupportedOperationException("Server should never reach client directly")
            }
        }
    }

    class Factory @Inject constructor(private val destination: String) : MessageAdapter.Factory {

        override fun create(type: Type, annotations: Array<Annotation>): MessageAdapter<Any> {
            return WebRtcMessageAdapter(
                destination,
                Gson(),
                GsonMessageAdapter.Factory().create(MessageDto::class.java, annotations)
            )
        }
    }
}