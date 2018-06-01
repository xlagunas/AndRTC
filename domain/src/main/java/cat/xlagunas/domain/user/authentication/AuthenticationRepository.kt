package cat.xlagunas.domain.user.authentication

import cat.xlagunas.domain.commons.User
import io.reactivex.Completable
import io.reactivex.Maybe

interface AuthenticationRepository {
    fun registerUser(user: User): Completable

    fun findUser(): Maybe<User>

    fun login(authenticationCredentials: AuthenticationCredentials): Completable
}