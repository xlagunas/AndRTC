package cat.xlagunas.domain.preferences

interface AuthTokenManager {
    fun authToken(): String?

    fun isAuthTokenAvailable(): Boolean

    fun insertAuthToken(token: String)

    fun deleteAuthToken()
}