package cat.xlagunas.user.core

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import cat.xlagunas.core.network.HttpClientFactory
import cat.xlagunas.core.persistence.AuthDataStore
import cat.xlagunas.core.persistence.db.UserEntity
import cat.xlagunas.core.persistence.db.VivDatabase
import cat.xlagunas.push.PushTokenApi
import cat.xlagunas.push.PushTokenRepositoryImpl
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.FakePushTokenProvider
import cat.xlagunas.test.utils.RecordingNavigator
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.user.UserConverter
import cat.xlagunas.user.auth.AuthHeaderInterceptor
import cat.xlagunas.user.auth.AuthPreferenceDataStore
import cat.xlagunas.user.auth.AuthenticationApi
import cat.xlagunas.user.auth.AuthenticationRepositoryImpl
import cat.xlagunas.user.login.LoginViewModel
import cat.xlagunas.user.profile.ProfileViewModel
import cat.xlagunas.user.register.RegisterViewModel
import com.google.gson.Gson

/**
 * The user feature wired by hand with its production classes. Only HTTP ([FakeBackend]),
 * navigation and Firebase are fakes.
 */
class UserCoreHarness(
    backend: FakeBackend,
    rule: CoreTestRule,
) {
    private val context: Context = ApplicationProvider.getApplicationContext()

    val navigator = RecordingNavigator()
    val pushTokenProvider = FakePushTokenProvider()

    // Same preferences file as ApplicationModule.provideSharedPreference.
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    val dataStore: AuthDataStore = AuthPreferenceDataStore(sharedPreferences)

    // Trampoline schedulers run some Room queries on the test (main) thread.
    val database: VivDatabase =
        Room.inMemoryDatabaseBuilder(context, VivDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    private val retrofit =
        HttpClientFactory.retrofit(
            backend.url,
            HttpClientFactory.okHttpClient(listOf(AuthHeaderInterceptor(dataStore))),
            Gson(),
        )

    private val authenticationRepository =
        AuthenticationRepositoryImpl(
            retrofit.create(AuthenticationApi::class.java),
            database.userDao(),
            UserConverter(),
            rule.rxSchedulers,
            dataStore,
            database,
            sharedPreferences,
        )

    private val pushTokenRepository =
        PushTokenRepositoryImpl(
            pushTokenProvider,
            retrofit.create(PushTokenApi::class.java),
            rule.rxSchedulers,
        )

    fun loginViewModel() = LoginViewModel(authenticationRepository, pushTokenRepository, navigator)

    fun registerViewModel() = RegisterViewModel(authenticationRepository, navigator)

    fun profileViewModel() = ProfileViewModel(authenticationRepository, pushTokenRepository, navigator)

    fun givenLoggedInUser(id: Long = 3) {
        database.userDao().insert(UserEntity(id, "anna", "Anna", "Puig", "anna@example.com", null))
        dataStore.insertAuthToken("token-123")
        dataStore.updateCurrentUserId(id)
    }

    fun close() {
        database.close()
    }
}
