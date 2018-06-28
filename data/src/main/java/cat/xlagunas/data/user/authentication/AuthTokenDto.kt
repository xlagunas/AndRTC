package cat.xlagunas.data.user.authentication

import com.google.gson.annotations.SerializedName

data class AuthTokenDto(@SerializedName("token") val token: String)