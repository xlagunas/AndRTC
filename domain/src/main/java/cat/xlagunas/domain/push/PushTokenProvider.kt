package cat.xlagunas.domain.push

interface PushTokenProvider {

    fun getPushToken(): String
    fun isTokenRegistered(): Boolean
    fun markTokenAsRegistered()
    fun invalidatePushToken()
}