package cat.xlagunas.user.data

import com.google.gson.annotations.SerializedName

data class AuthTokenDto(@SerializedName("token") val token: String)