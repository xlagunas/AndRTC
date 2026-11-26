package cat.xlagunas.user.core

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import assertk.assertions.single
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.NavigationCall
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.FakeResponse
import cat.xlagunas.test.utils.http.disconnect
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.malformedJson
import cat.xlagunas.test.utils.http.ok
import cat.xlagunas.test.utils.recordStates
import cat.xlagunas.user.login.InvalidLoginState
import cat.xlagunas.user.login.LoginState
import cat.xlagunas.user.login.SuccessLoginState
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class LoginCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    private lateinit var harness: UserCoreHarness

    @Before
    fun setUp() {
        harness = UserCoreHarness(backend, rule)
    }

    @After
    fun tearDown() {
        harness.close()
    }

    @Test
    fun givenValidCredentials_whenLogin_thenSuccessAndNavigatesToProfile() {
        givenBackendAcceptsLogin()

        val states = login()

        assertThat(states.awaitCount(1)).containsExactly(SuccessLoginState)
        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.ToProfile)
    }

    @Test
    fun givenValidCredentials_whenLogin_thenSendsCredentialsAndAuthorizedRequests() {
        givenBackendAcceptsLogin()

        login().awaitCount(1)

        val auth = backend.awaitRequest()
        assertThat(auth.method).isEqualTo("POST")
        assertThat(auth.path).isEqualTo("/auth/")
        assertThat(auth.body).isEqualTo("""{"username":"anna","password":"secret"}""")
        val user = backend.awaitRequest()
        assertThat(user.method).isEqualTo("GET")
        assertThat(user.path).isEqualTo("/user/")
        assertThat(user.header("Authorization")).isEqualTo("token-123")
        val token = backend.awaitRequest()
        assertThat(token.method).isEqualTo("PUT")
        assertThat(token.path).isEqualTo("/token/")
        assertThat(token.body).isEqualTo("""{"value":"push-token-1","platform":"ANDROID"}""")
        assertThat(token.header("Authorization")).isEqualTo("token-123")
    }

    @Test
    fun givenValidCredentials_whenLogin_thenSessionPersisted() {
        givenBackendAcceptsLogin()

        login().awaitCount(1)

        assertThat(harness.dataStore.authToken()).isEqualTo("token-123")
        assertThat(harness.dataStore.getCurrentUserId()).isEqualTo(3L)
        assertThat(harness.database.userDao().loadAllByUserId(3).map { it.username }).containsExactly("anna")
        assertThat(harness.pushTokenProvider.isTokenRegistered()).isTrue()
    }

    @Test
    fun givenUnauthorized_whenLogin_thenAuthenticationErrorAndNoSession() {
        backend.respondTo("POST", "/auth/", httpError(401))

        val states = login()

        assertThat(states.awaitCount(1)).containsExactly(InvalidLoginState("Authentication error"))
        assertNoNavigation()
        assertThat(harness.dataStore.isAuthTokenAvailable()).isFalse()
        assertThat(backend.requests.map { it.path }).containsExactly("/auth/")
    }

    @Test
    fun givenConflict_whenLogin_thenAlreadyRegisteredError() {
        backend.respondTo("POST", "/auth/", httpError(409))

        assertThat(login().awaitCount(1)).containsExactly(InvalidLoginState("User already registered"))
        assertNoNavigation()
    }

    @Test
    fun givenServerError_whenLogin_thenErrorAndNoNavigation() {
        backend.respondTo("POST", "/auth/", httpError(500))

        assertThat(login().awaitCount(1)).single().isInstanceOf(InvalidLoginState::class)
        assertNoNavigation()
    }

    @Test
    fun givenDisconnect_whenLogin_thenErrorAndNoNavigation() {
        backend.respondTo("POST", "/auth/", disconnect())

        assertThat(login().awaitCount(1)).single().isInstanceOf(InvalidLoginState::class)
        assertNoNavigation()
    }

    @Test
    fun givenMalformedAuthResponse_whenLogin_thenErrorAndNoNavigation() {
        backend.respondTo("POST", "/auth/", malformedJson())

        assertThat(login().awaitCount(1)).single().isInstanceOf(InvalidLoginState::class)
        assertNoNavigation()
    }

    @Test
    fun givenUserFetchFails_whenLogin_thenErrorAndNoUserPersisted() {
        backend.respondTo("POST", "/auth/", ok("user/auth_token.json"))
        backend.respondTo("GET", "/user/", httpError(500))

        assertThat(login().awaitCount(1)).single().isInstanceOf(InvalidLoginState::class)
        assertNoNavigation()
        assertThat(harness.dataStore.getCurrentUserId()).isEqualTo(-1L)
        assertThat(harness.database.userDao().loadAllByUserId(3)).isEmpty()
        assertThat(harness.dataStore.isAuthTokenAvailable()).isFalse()
    }

    @Test
    fun givenForbidden_whenLogin_thenErrorAndNoNavigation() {
        backend.respondTo("POST", "/auth/", httpError(403))

        assertThat(login().awaitCount(1)).single().isInstanceOf(InvalidLoginState::class)
        assertNoNavigation()
        assertThat(harness.dataStore.isAuthTokenAvailable()).isFalse()
    }

    @Test
    fun givenUserFetchUnauthorized_whenLogin_thenAuthenticationErrorAndNoSession() {
        givenUserFetchFailsWith(httpError(401))

        assertThat(login().awaitCount(1)).containsExactly(InvalidLoginState("Authentication error"))
        assertNoSession()
    }

    @Test
    fun givenUserFetchDisconnects_whenLogin_thenErrorAndNoSession() {
        givenUserFetchFailsWith(disconnect())

        assertThat(login().awaitCount(1)).single().isInstanceOf(InvalidLoginState::class)
        assertNoSession()
    }

    @Test
    fun givenMalformedUserResponse_whenLogin_thenErrorAndNoSession() {
        givenUserFetchFailsWith(malformedJson())

        assertThat(login().awaitCount(1)).single().isInstanceOf(InvalidLoginState::class)
        assertNoSession()
    }

    @Test
    fun givenPushRegistrationDisconnects_whenLogin_thenStillLoggedIn() {
        givenBackendAcceptsLogin()
        backend.respondTo("PUT", "/token/", disconnect())

        assertThat(login().awaitCount(1)).containsExactly(SuccessLoginState)
        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.ToProfile)
        assertThat(harness.pushTokenProvider.isTokenRegistered()).isFalse()
    }

    @Test
    fun givenPushRegistrationFails_whenLogin_thenStillLoggedIn() {
        givenBackendAcceptsLogin()
        backend.respondTo("PUT", "/token/", httpError(500))

        assertThat(login().awaitCount(1)).containsExactly(SuccessLoginState)
        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.ToProfile)
        assertThat(harness.pushTokenProvider.isTokenRegistered()).isFalse()
    }

    @Test
    fun givenPushTokenUnavailable_whenLogin_thenStillLoggedInWithoutTokenRequest() {
        givenBackendAcceptsLogin()
        harness.pushTokenProvider.failure = IOException("Firebase unavailable")

        assertThat(login().awaitCount(1)).containsExactly(SuccessLoginState)
        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.ToProfile)
        assertThat(backend.requests.map { it.path }).containsExactly("/auth/", "/user/")
    }

    @Test
    fun givenLoginScreen_whenNavigateToRegistration_thenRegistrationShown() {
        harness.loginViewModel().navigateToRegistration()

        assertThat(harness.navigator.calls.values).containsExactly(NavigationCall.ToRegistration)
    }

    private fun givenBackendAcceptsLogin() {
        backend.respondTo("POST", "/auth/", ok("user/auth_token.json"))
        backend.respondTo("GET", "/user/", ok("user/user_anna.json"))
        backend.respondTo("PUT", "/token/", ok())
    }

    private fun login() =
        harness.loginViewModel().let { viewModel ->
            recordStates<LoginState>(viewModel.onLoginStateChange()).also { viewModel.login("anna", "secret") }
        }

    private fun givenUserFetchFailsWith(response: FakeResponse) {
        backend.respondTo("POST", "/auth/", ok("user/auth_token.json"))
        backend.respondTo("GET", "/user/", response)
    }

    private fun assertNoSession() {
        assertNoNavigation()
        assertThat(harness.dataStore.isAuthTokenAvailable()).isFalse()
        assertThat(harness.dataStore.getCurrentUserId()).isEqualTo(-1L)
        assertThat(harness.database.userDao().loadAllByUserId(3)).isEmpty()
    }

    private fun assertNoNavigation() {
        assertThat(harness.navigator.calls.values).hasSize(0)
    }
}
