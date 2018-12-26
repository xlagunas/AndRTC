package cat.xlagunas.conference.domain

import com.google.gson.annotations.SerializedName

data class RoomDetails(@SerializedName("participants") val users: List<RoomParticipant>)