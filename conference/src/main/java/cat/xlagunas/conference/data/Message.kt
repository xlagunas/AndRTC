package cat.xlagunas.conference.data

import com.google.gson.annotations.SerializedName

data class Message(@SerializedName("from") val from: String, @SerializedName("data") val data: String, @SerializedName("destination") val destination: String)