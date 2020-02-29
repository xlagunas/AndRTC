package cat.xlagunas.call

import cat.xlagunas.core.data.net.FriendDto
import cat.xlagunas.core.domain.entity.Call
import cat.xlagunas.push.CallMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import javax.inject.Inject

class CallConverter @Inject constructor(private val gson: Gson) {
    fun toCallParticipantsDto(friendParticipantsList: List<FriendDto>): CallParticipantsDto {
        return CallParticipantsDto(friendParticipantsList.map(this::toCallParticipantDto).toList())
    }

    private fun toCallParticipantDto(friendParticipant: FriendDto): CallParticipantDto {
        return CallParticipantDto(friendParticipant.id)
    }

    fun toCall(callDto: CallDto): Call {
        return Call(callDto.roomId)
    }

    fun toCall(message: CallMessage): Call {
        val msgAsJson = gson.fromJson(message.params, JsonObject::class.java)
            .getAsJsonObject("content")
        val call = gson.fromJson(msgAsJson, CallDto::class.java)
        return toCall(call)
    }
}