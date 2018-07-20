package cat.xlagunas.data.push

import com.google.gson.annotations.SerializedName

data class PushTokenDto(
    @SerializedName("value") private val value: String,
    @SerializedName("platform") private val platform: String = "ANDROID"
)