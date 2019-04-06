package cat.xlagunas.data.user.authentication

import android.content.SharedPreferences
import androidx.core.content.edit
import cat.xlagunas.core.domain.auth.AuthTokenDataStore
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.user.authentication.AuthenticationCredentials
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import timber.log.Timber
import javax.inject.Inject

class AuthenticationRepositoryImpl
@Inject constructor(
    private val authenticationApi: AuthenticationApi,
    private val userDao: cat.xlagunas.core.data.db.UserDao,
    private val userConverter: cat.xlagunas.core.data.converter.UserConverter,
    private val schedulers: RxSchedulers,
    private val tokenDataStore: AuthTokenDataStore,
    private val vivDatabase: cat.xlagunas.core.data.db.VivDatabase,
    private val sharedPreferences: SharedPreferences
) : AuthenticationRepository {

    override fun findUser(): Maybe<User> {
        return userDao.user
            .map(userConverter::toUser)
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun isUserLoggedIn(): Flowable<Boolean> {
        return userDao.getUserCount()
            .map { count -> count > 0 }
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun getUser(): Flowable<User> {
        return userDao.getUserHot()
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
        return authenticationApi.loginUser(
            AuthDto(authenticationCredentials.username,
                authenticationCredentials.password
            )
        )
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

    override fun logoutUser(): Completable {
        return Completable.fromAction { deleteAuthToken() }
            .andThen(Completable.fromAction { vivDatabase.clearAllTables() })
            .andThen(Completable.fromAction { sharedPreferences.edit { clear() } })
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }
}
