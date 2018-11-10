package cat.xlagunas.core.data.net

import com.google.gson.annotations.SerializedName

data class FriendDto(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("profilePic") val profilePic: String?,
    @SerializedName("status") val relationship: Relationship
)

enum class Relationship { NONE, REQUESTED, PENDING, ACCEPTED }