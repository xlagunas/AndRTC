package cat.xlagunas.data.common.net

import com.google.gson.annotations.SerializedName

data class UserDto(
        @SerializedName("id") val id: Long?,
        @SerializedName("username") val username: String,
        @SerializedName("firstname") val firstName: String,
        @SerializedName("lastname") val lastName: String,
        @SerializedName("email") val email: String,
        @SerializedName("profilePic") val profilePic: String?,
        @SerializedName("password") val password: String?)