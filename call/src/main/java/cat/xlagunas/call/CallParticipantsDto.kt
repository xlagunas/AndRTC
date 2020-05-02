package cat.xlagunas.call

import com.google.gson.annotations.SerializedName

data class CallParticipantsDto(@SerializedName("participants") val participants: List<CallParticipantDto>)

data class CallParticipantDto(@SerializedName("id") val id: Long)
