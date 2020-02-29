package cat.xlagunas.user.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import cat.xlagunas.core.data.converter.UserConverter
import cat.xlagunas.core.data.db.UserDao
import cat.xlagunas.core.domain.auth.AuthDataStore
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.user.domain.AuthenticationCredentials
import cat.xlagunas.user.domain.AuthenticationRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class AuthenticationRepositoryImplTest {

    private lateinit var authenticationRepository: AuthenticationRepository

    private lateinit var userDao: UserDao

    private lateinit var userConverter: UserConverter

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authenticationApi: AuthenticationApi

    @Mock
    private lateinit var authDataStore: AuthDataStore

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.application, cat.xlagunas.core.data.db.VivDatabase::class.java
        )
            .allowMainThreadQueries().build()
        userDao = database.userDao()
        userConverter = UserConverter()
        authenticationRepository = AuthenticationRepositoryImpl(
            authenticationApi,
            userDao,
            userConverter,
            RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline()),
            authDataStore,
            database,
            RuntimeEnvironment.application.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        )
    }

    @Test
    fun whenSuccessfullyRegistered_thenUserPersisted() {
        val user = User("Xavier", "Lagunas", "Calpe", "xlagunas@gmail.com", "image@gmail.com", "123456")
        `when`(authenticationApi.registerUser(userConverter.toUserDto(user)))
            .thenReturn(Completable.fromAction { userConverter.toUserEntity(user) })

        authenticationRepository.registerUser(user).test()

        userDao.loadAll().test().assertValueCount(1)
    }

    @Test
    fun whenErrorRegisteringUser_thenUserNotPersisted() {
        val user = User("Xavier", "Lagunas", "Calpe", "xlagunas@gmail.com", "image@gmail.com", "123456")
        `when`(authenticationApi.registerUser(userConverter.toUserDto(user))).thenReturn(Completable.error(IOException("Error")))

        authenticationRepository.registerUser(user)
            .test().assertError(IOException::class.java)

        userDao.loadAll().test()
            .assertValueCount(0)
    }

    @Test
    fun givenSuccessfulAuthentication_thenTokenPersisted() {
        val authToken = "thisIsTheAuthTokenKeyReturnedByTheServer"
        val authenticationCredentials = AuthenticationCredentials("aUser", "aPass")
        `when`(
            authenticationApi.loginUser(
                AuthDto(
                    authenticationCredentials.username,
                    authenticationCredentials.password
                )
            )
        ).thenReturn(Single.just(AuthTokenDto(authToken)))
        `when`(authenticationApi.getUser()).thenReturn(Single.just(mockedUserDto()))

        authenticationRepository.login(authenticationCredentials)
            .test().assertComplete()

        verify(authDataStore).insertAuthToken(authToken)
    }

    @Test
    fun givenInvalidAuthentication_thenTokenNotPersisted() {
        val authenticationCredentials = AuthenticationCredentials("aUser", "aPass")

        `when`(
            authenticationApi.loginUser(
                AuthDto(
                    authenticationCredentials.username,
                    authenticationCredentials.password
                )
            )
        ).thenReturn(Single.error(IOException("Error!!")))

        authenticationRepository.login(authenticationCredentials).test().assertError(IOException::class.java)

        verify(authDataStore, never()).insertAuthToken(ArgumentMatchers.anyString())
    }

    private fun mockedUserDto() =
        cat.xlagunas.core.data.net.UserDto(10, "fakeUser", "Name", "Surname", "email@email.com", null, null)

    @Test
    fun reactive_insert() {
        val observer = userDao.getUserCount().test()
        observer.assertValue(0)

        userDao.insert(userConverter.toUserEntity(mockedUserDto()))
        observer.assertValueAt(1, 1)
        userDao.delete(userConverter.toUserEntity(mockedUserDto()))
        observer.assertValueAt(2, 0)
    }
}