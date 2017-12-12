package cat.xlagunas.domain.user.register

import cat.xlagunas.domain.commons.User
import io.reactivex.Completable

interface RegisterRepository {
    fun registerUser(user: User): Completable
}