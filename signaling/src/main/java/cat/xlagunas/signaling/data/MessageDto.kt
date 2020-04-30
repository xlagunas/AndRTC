package cat.xlagunas.signaling.data

import cat.xlagunas.signaling.domain.MessageType
import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("from")
    val localId: String,
    @SerializedName("to")
    val destinationId: String,
    @SerializedName("type")
    val type: MessageType,
    @SerializedName("data")
    val data: String
)