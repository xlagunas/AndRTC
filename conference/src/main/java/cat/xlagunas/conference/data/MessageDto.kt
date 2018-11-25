package cat.xlagunas.conference.data

import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("from") val from: String,
    @SerializedName("data") val data: String,
    @SerializedName("type") val type: MessageType,
    @SerializedName("destination") val destination: String
)

enum class MessageType {
    ICE_CANDIDATE,
    SESSION_DESCRIPTION,
    SERVER
}
