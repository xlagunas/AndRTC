package cat.xlagunas.core.domain.auth

interface AuthTokenDataStore {

    fun authToken(): String?

    fun isAuthTokenAvailable(): Boolean

    fun insertAuthToken(token: String)

    fun deleteAuthToken()
}