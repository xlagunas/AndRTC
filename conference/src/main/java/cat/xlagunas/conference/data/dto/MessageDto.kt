package cat.xlagunas.conference.data.dto

import cat.xlagunas.conference.domain.model.MessageType
import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("from") val from: String,
    @SerializedName("data") val data: String,
    @SerializedName("type") val type: MessageType,
    @SerializedName("destination") val destination: String
)

