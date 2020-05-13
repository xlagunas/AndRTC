package cat.xlagunas.user.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import cat.xlagunas.core.persistence.AuthDataStore
import cat.xlagunas.core.scheduler.RxSchedulers
import cat.xlagunas.user.User
import cat.xlagunas.user.UserConverter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject
import timber.log.Timber

class AuthenticationRepositoryImpl
@Inject constructor(
    private val authenticationApi: AuthenticationApi,
    private val userDao: cat.xlagunas.core.persistence.db.UserDao,
    private val userConverter: UserConverter,
    private val schedulers: RxSchedulers,
    private val dataStore: AuthDataStore,
    private val vivDatabase: cat.xlagunas.core.persistence.db.VivDatabase,
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

        return authenticationApi.registerUser(userDto)
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun login(authenticationCredentials: AuthenticationCredentials): Completable {
        return authenticationApi.loginUser(
            AuthDto(
                authenticationCredentials.username,
                authenticationCredentials.password
            )
        )
            .doOnSuccess { insertAuthToken(it.token) }
            .flatMap { authenticationApi.getUser() }
            .map { userConverter.toUserEntity(it) }
            .doOnSuccess(userDao::insert)
            .doOnSuccess { dataStore.updateCurrentUserId(it.id!!) }
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
            .doOnSuccess { Timber.i("Successfully logged in user") }
            .ignoreElement()
    }

    override fun authToken(): String? = dataStore.authToken()

    override fun isAuthTokenAvailable(): Boolean = dataStore.isAuthTokenAvailable()

    override fun insertAuthToken(token: String) = dataStore.insertAuthToken(token)

    override fun deleteAuthToken() = dataStore.deleteAuthToken()

    override fun refreshToken(): Completable {
        return authenticationApi.refreshUserToken()
            .doOnSuccess { insertAuthToken(it.token) }
            .doOnSuccess { Timber.i("Successfully refreshed token") }
            .ignoreElement()
    }

    override fun logoutUser(): Completable {
        return Completable.fromAction { deleteAuthToken() }
            .andThen(Completable.fromAction { deleteUserPreferences() })
            .andThen(Completable.fromAction { vivDatabase.clearAllTables() })
            .andThen(Completable.fromAction { sharedPreferences.edit { clear() } })
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    private fun deleteUserPreferences() {
        return dataStore.deleteUserPreferences()
    }
}
