package cat.xlagunas.push

interface PushTokenProvider {
    fun getPushToken(): String

    fun isTokenRegistered(): Boolean

    fun markTokenAsRegistered()

    fun clearTokenRegistration()

    fun invalidatePushToken()
}
