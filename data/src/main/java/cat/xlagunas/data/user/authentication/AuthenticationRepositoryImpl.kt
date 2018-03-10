package cat.xlagunas.data.user.authentication

import cat.xlagunas.data.common.converter.UserConverter
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.preferences.AuthTokenManager
import cat.xlagunas.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.user.authentication.AuthenticationCredentials
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import io.reactivex.Completable
import io.reactivex.Flowable

class AuthenticationRepositoryImpl(
        private val authenticationApi: AuthenticationApi,
        private val userDao: UserDao,
        private val userConverter: UserConverter,
        private val schedulers: RxSchedulers,
        private val authTokenManager: AuthTokenManager) : AuthenticationRepository {

    override fun findUser(): Flowable<User> {
        return userDao.loadAll()
                .map(userConverter::toUser)
    }

    override fun registerUser(user: User): Completable {
        val userDto = userConverter.toUserDto(user)
        val userEntity = userConverter.toUserEntity(user)

        return authenticationApi.registerUser(userDto)
                .andThen(Completable.fromAction { userDao.insert(userEntity) })
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
    }

    override fun login(authenticationCredentials: AuthenticationCredentials): Completable {
        return authenticationApi.loginUser(authenticationCredentials)
                .flatMapCompletable { Completable.fromCallable { authTokenManager.insertAuthToken(it.token) } }
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
    }
}