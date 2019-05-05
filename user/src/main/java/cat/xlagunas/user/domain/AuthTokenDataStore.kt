package cat.xlagunas.user.domain

interface AuthTokenDataStore {

    fun authToken(): String?

    fun isAuthTokenAvailable(): Boolean

    fun insertAuthToken(token: String)

    fun deleteAuthToken()
}