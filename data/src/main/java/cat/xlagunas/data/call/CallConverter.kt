package cat.xlagunas.data.call

import cat.xlagunas.core.data.net.FriendDto
import cat.xlagunas.domain.call.Call

class CallConverter {
    fun toCallParticipantsDto(friendParticipantsList: List<FriendDto>): CallParticipantsDto {
        return CallParticipantsDto(friendParticipantsList.map(this::toCallParticipantDto).toList())
    }

    private fun toCallParticipantDto(friendParticipant: FriendDto): CallParticipantDto {
        return CallParticipantDto(friendParticipant.id)
    }

    fun toCall(callDto: CallDto): Call {
        return Call(callDto.roomId)
    }
}