package cat.xlagunas.data.user.authentication

import cat.xlagunas.data.common.converter.UserConverter
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.preferences.AuthTokenManager
import cat.xlagunas.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.user.authentication.AuthenticationCredentials
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import retrofit2.HttpException
import timber.log.Timber

class AuthenticationRepositoryImpl(
        private val authenticationApi: AuthenticationApi,
        private val userDao: UserDao,
        private val userConverter: UserConverter,
        private val schedulers: RxSchedulers,
        private val authTokenManager: AuthTokenManager) : AuthenticationRepository {

    override fun findUser(): Maybe<User> {
        return userDao.user
                .switchIfEmpty(authenticationApi.getUser().map { userConverter.toUserEntity(it) }.toMaybe())
                .onErrorResumeNext { t: Throwable ->
                    if (t is HttpException && t.code() == 401) {
                        Maybe.empty()
                    } else {
                        Maybe.error(t)
                    }
                }
                .map(userConverter::toUser)
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
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
                .doOnSuccess { authTokenManager.insertAuthToken(it.token) }
                .flatMap { authenticationApi.getUser() }
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
                .doOnSuccess { Timber.i("Successfully logged in user") }
                .toCompletable()
    }
}