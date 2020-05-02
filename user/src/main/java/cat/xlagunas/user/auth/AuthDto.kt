package cat.xlagunas.user.auth

import com.google.gson.annotations.SerializedName

data class AuthDto(@SerializedName("username") private val username: String, @SerializedName("password") private val password: String)
