package cat.xlagunas.conference.data.dto.mapper

import cat.xlagunas.conference.data.dto.MessageDto
import cat.xlagunas.conference.domain.model.IceCandidateMessage
import cat.xlagunas.conference.domain.model.MessageType
import cat.xlagunas.conference.domain.utils.UserSessionIdentifier
import com.google.gson.Gson
import org.webrtc.IceCandidate
import javax.inject.Inject

class MessageDtoMapper @Inject constructor(
    private val gson: Gson,
    private val userSessionIdentifier: UserSessionIdentifier
) {
    fun createIceCandidateMessage(iceCandidate: IceCandidate, destinationUserId: String): MessageDto {
        return MessageDto(
            from = userSessionIdentifier.getUserId(),
            data = gson.toJson(IceCandidateMessage(iceCandidate, userSessionIdentifier.getUserId())),
            type = MessageType.ICE_CANDIDATE,
            destination = destinationUserId
        )
    }

    //TODO Investigate if Any is using reflection and what is the impact to serialise it
    fun createMessageDto(body: Any, type: MessageType, destinationUserId: String): MessageDto {
        return MessageDto(
            from = userSessionIdentifier.getUserId(),
            data = gson.toJson(body),
            type = type,
            destination = destinationUserId
        )
    }
}