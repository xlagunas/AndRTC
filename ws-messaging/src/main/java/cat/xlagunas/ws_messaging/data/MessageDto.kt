package cat.xlagunas.ws_messaging.data

import cat.xlagunas.ws_messaging.model.MessageType
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