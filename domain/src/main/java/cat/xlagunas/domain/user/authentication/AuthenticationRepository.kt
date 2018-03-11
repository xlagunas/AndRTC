package cat.xlagunas.domain.user.authentication

import cat.xlagunas.domain.commons.User
import io.reactivex.Completable
import io.reactivex.Flowable

interface AuthenticationRepository {
    fun registerUser(user: User): Completable

    fun findUser(): Flowable<User>

    fun login(authenticationCredentials: AuthenticationCredentials): Completable
}