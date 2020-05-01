package cat.xlagunas.user.auth

import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.user.auth.AuthenticationCredentials
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface AuthenticationRepository {
    fun registerUser(user: User): Completable

    fun findUser(): Maybe<User>

    fun getUser(): Flowable<User>

    fun login(authenticationCredentials: AuthenticationCredentials): Completable

    fun authToken(): String?

    fun isAuthTokenAvailable(): Boolean

    fun insertAuthToken(token: String)

    fun deleteAuthToken()

    fun refreshToken(): Completable

    fun isUserLoggedIn(): Flowable<Boolean>

    fun logoutUser(): Completable
}