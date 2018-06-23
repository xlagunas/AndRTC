package cat.xlagunas.data.common.net

import com.google.gson.annotations.SerializedName

data class FriendDto(
        val id: Long,
        val username: String,
        val name: String,
        val email: String,
        val profilePic: String?,
        @SerializedName("status")
        val relationship: Relationship)

enum class Relationship { NONE, REQUESTED }