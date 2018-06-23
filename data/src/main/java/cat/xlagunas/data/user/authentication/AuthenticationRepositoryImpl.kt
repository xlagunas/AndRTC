package cat.xlagunas.data.user.authentication

import cat.xlagunas.data.common.converter.UserConverter
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.data.push.PushTokenDto
import cat.xlagunas.data.push.TokenNotFoundException
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.push.PushTokenProvider
import cat.xlagunas.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.user.authentication.AuthTokenDataStore
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
        private val tokenDataStore: AuthTokenDataStore,
        private val pushTokenProvider: PushTokenProvider) : AuthenticationRepository {

    override fun findUser(): Maybe<User> {
        return userDao.user
                .switchIfEmpty(findRemoteUser())
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

    private fun findRemoteUser(): Maybe<cat.xlagunas.data.common.db.UserEntity> =
            authenticationApi.getUser().map { userConverter.toUserEntity(it) }.toMaybe()


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
                .doOnSuccess { insertAuthToken(it.token) }
                .flatMap { authenticationApi.getUser() }
                .map { userConverter.toUserEntity(it) }
                .map(userDao::insert)
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
                .doOnSuccess { Timber.i("Successfully logged in user") }
                .ignoreElement()
    }


    override fun authToken(): String? = tokenDataStore.authToken()

    override fun isAuthTokenAvailable(): Boolean = tokenDataStore.isAuthTokenAvailable()

    override fun insertAuthToken(token: String) = tokenDataStore.insertAuthToken(token)

    override fun deleteAuthToken() = tokenDataStore.deleteAuthToken()

    override fun refreshToken(): Completable {
        return authenticationApi.refreshUserToken()
                .doOnSuccess { insertAuthToken(it.token) }
                .doOnSuccess { Timber.i("Successfully refreshed token") }
                .ignoreElement()
    }

    override fun isPushTokenRegistered() = pushTokenProvider.isTokenRegistered()

    override fun markPushTokenAsRegistered() = pushTokenProvider.markTokenAsRegistered()

    override fun registerPushToken(): Completable {
        val token = pushTokenProvider.getPushToken()
        return if (token != null) {
            authenticationApi.addPushToken(PushTokenDto(token))
                    .doOnComplete { pushTokenProvider.markTokenAsRegistered() }
                    .doOnSubscribe { Timber.d("Starting token registration") }
                    .doOnComplete { Timber.d("Push token $token successfully registered") }
                    .observeOn(schedulers.mainThread)
                    .subscribeOn(schedulers.io)
        } else {
            val ex = TokenNotFoundException()
            Completable
                    .error(ex)
                    .doOnError { Timber.e(ex, "Token not found") }
        }
    }
}

