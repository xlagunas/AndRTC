package cat.xlagunas.conference.data.dto

import com.google.gson.annotations.SerializedName

data class Conferencee(@SerializedName("participants") val users: List<ConferenceeDto>)