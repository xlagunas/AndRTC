package cat.xlagunas.user.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.user.auth.AuthenticationCredentials
import cat.xlagunas.user.auth.AuthenticationRepository
import io.reactivex.Completable
import okhttp3.ResponseBody.Companion.toResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class LoginViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val authenticationRepository = mock(AuthenticationRepository::class.java)
    private val pushTokenRepository = mock(PushTokenRepository::class.java)
    private val navigator = mock(Navigator::class.java)

    private lateinit var viewModel: LoginViewModel
    private val states = mutableListOf<LoginState>()

    private val credentials = AuthenticationCredentials(USERNAME, PASSWORD)

    @Before
    fun setUp() {
        viewModel = LoginViewModel(authenticationRepository, pushTokenRepository, navigator)
        viewModel.onLoginStateChange().observeForever { states.add(it) }
    }

    @Test
    fun givenValidCredentials_whenLogin_thenSuccessStateAndNavigateToProfile() {
        var pushTokenRegistered = false
        `when`(authenticationRepository.login(credentials)).thenReturn(Completable.complete())
        `when`(pushTokenRepository.registerPushToken())
            .thenReturn(Completable.fromAction { pushTokenRegistered = true })

        viewModel.login(USERNAME, PASSWORD)

        assertThat(states).containsExactly(SuccessLoginState)
        assertThat(pushTokenRegistered).isTrue()
        verify(navigator).navigateToProfile()
    }

    @Test
    fun givenUnauthorized_whenLogin_thenAuthenticationError() {
        givenLoginFailsWith(httpException(401))

        viewModel.login(USERNAME, PASSWORD)

        assertThat(states).containsExactly(InvalidLoginState("Authentication error"))
        verifyNoInteractions(navigator)
    }

    @Test
    fun givenConflict_whenLogin_thenAlreadyRegisteredError() {
        givenLoginFailsWith(httpException(409))

        viewModel.login(USERNAME, PASSWORD)

        assertThat(states).containsExactly(InvalidLoginState("User already registered"))
        verifyNoInteractions(navigator)
    }

    @Test
    fun givenOtherHttpError_whenLogin_thenHttpMessageAsError() {
        val exception = httpException(500)
        givenLoginFailsWith(exception)

        viewModel.login(USERNAME, PASSWORD)

        assertThat(states).containsExactly(InvalidLoginState(exception.message()))
        verifyNoInteractions(navigator)
    }

    @Test
    fun givenNetworkError_whenLogin_thenExceptionMessageAsError() {
        givenLoginFailsWith(IOException("No connection"))

        viewModel.login(USERNAME, PASSWORD)

        assertThat(states).containsExactly(InvalidLoginState("No connection"))
        verifyNoInteractions(navigator)
    }

    @Test
    fun givenErrorWithoutMessage_whenLogin_thenGenericError() {
        givenLoginFailsWith(IllegalStateException())

        viewModel.login(USERNAME, PASSWORD)

        assertThat(states).containsExactly(InvalidLoginState("Something went wrong"))
    }

    @Test
    fun givenLoginFails_whenLogin_thenPushTokenNotRegistered() {
        var pushTokenRegistered = false
        `when`(authenticationRepository.login(credentials))
            .thenReturn(Completable.error(IOException("No connection")))
        `when`(pushTokenRepository.registerPushToken())
            .thenReturn(Completable.fromAction { pushTokenRegistered = true })

        viewModel.login(USERNAME, PASSWORD)

        assertThat(pushTokenRegistered).isFalse()
    }

    @Test
    fun givenPushTokenRegistrationFails_whenLogin_thenStillSuccessAndNavigateToProfile() {
        `when`(authenticationRepository.login(credentials)).thenReturn(Completable.complete())
        `when`(pushTokenRepository.registerPushToken())
            .thenReturn(Completable.error(IOException("Push registration failed")))

        viewModel.login(USERNAME, PASSWORD)

        assertThat(states).containsExactly(SuccessLoginState)
        verify(navigator).navigateToProfile()
    }

    @Test
    fun whenNavigateToRegistration_thenNavigatorCalled() {
        viewModel.navigateToRegistration()

        verify(navigator).navigateToRegistration()
    }

    private fun givenLoginFailsWith(throwable: Throwable) {
        `when`(authenticationRepository.login(credentials)).thenReturn(Completable.error(throwable))
        `when`(pushTokenRepository.registerPushToken()).thenReturn(Completable.complete())
    }

    private fun httpException(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    private companion object {
        const val USERNAME = "aUser"
        const val PASSWORD = "aPassword"
    }
}
