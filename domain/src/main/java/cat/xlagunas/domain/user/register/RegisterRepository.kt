package cat.xlagunas.domain.user.register

import cat.xlagunas.domain.commons.User
import io.reactivex.Completable
import io.reactivex.Flowable

interface RegisterRepository {
    fun registerUser(user: User): Completable

    fun findUser(): Flowable<User>
}