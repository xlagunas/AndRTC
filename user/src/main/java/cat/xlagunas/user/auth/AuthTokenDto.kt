package cat.xlagunas.user.auth

import com.google.gson.annotations.SerializedName

data class AuthTokenDto(@SerializedName("token") val token: String)