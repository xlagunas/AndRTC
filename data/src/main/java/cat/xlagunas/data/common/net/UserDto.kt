package cat.xlagunas.data.common.net

import com.google.gson.annotations.SerializedName

data class UserDto(val id: Long?,
                   val username: String,
                   @SerializedName("firstname")
                   val firstName: String,
                   @SerializedName("lastname")
                   val lastName: String,
                   val email: String,
                   val profilePic: String?,
                   val password: String?)