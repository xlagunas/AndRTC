package cat.xlagunas.conference.domain

import com.google.gson.annotations.SerializedName

data class RoomParticipant(@SerializedName("userId") val userId: String)
