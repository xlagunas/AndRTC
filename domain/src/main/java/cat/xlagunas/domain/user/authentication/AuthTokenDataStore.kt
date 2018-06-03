package cat.xlagunas.domain.user.authentication

interface AuthTokenDataStore {

    fun authToken(): String?

    fun isAuthTokenAvailable(): Boolean

    fun insertAuthToken(token: String)

    fun deleteAuthToken()

}