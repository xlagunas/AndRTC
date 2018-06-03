package cat.xlagunas.domain.user.authentication

import cat.xlagunas.domain.commons.User
import io.reactivex.Completable
import io.reactivex.Maybe

interface AuthenticationRepository {
    fun registerUser(user: User): Completable

    fun findUser(): Maybe<User>

    fun login(authenticationCredentials: AuthenticationCredentials): Completable

    fun authToken(): String?

    fun isAuthTokenAvailable(): Boolean

    fun insertAuthToken(token: String)

    fun deleteAuthToken()

    fun refreshToken(): Completable
}