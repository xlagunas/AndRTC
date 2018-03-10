package cat.xlagunas.data.user.register

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import cat.xlagunas.data.BuildConfig
import cat.xlagunas.data.common.converter.UserConverter
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.data.common.db.VivDatabase
import cat.xlagunas.data.common.preferences.AuthTokenManagerImpl
import cat.xlagunas.data.user.authentication.AuthenticationRepositoryImpl
import cat.xlagunas.data.user.authentication.AuthenticationApi
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.preferences.AuthTokenManager
import cat.xlagunas.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException
import org.junit.Rule



@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class AuthenticationRepositoryImplTest {

    private lateinit var authenticationRepository: AuthenticationRepository

    private lateinit var userDao: UserDao

    private lateinit var userConverter: UserConverter

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authenticationApi: AuthenticationApi

    @Mock
    private lateinit var authTokenManager: AuthTokenManager


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val database = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.application, VivDatabase::class.java).allowMainThreadQueries().build()
        userDao = database.userDao()
        userConverter = UserConverter()
        authenticationRepository = AuthenticationRepositoryImpl(authenticationApi, userDao, userConverter, RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline()), authTokenManager)
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

}