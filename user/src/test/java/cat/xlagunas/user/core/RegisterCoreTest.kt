package cat.xlagunas.user.core

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.prop
import assertk.assertions.single
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.NavigationCall
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.disconnect
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.ok
import cat.xlagunas.test.utils.recordStates
import cat.xlagunas.user.User
import cat.xlagunas.user.register.RegistrationError
import cat.xlagunas.user.register.RegistrationState
import cat.xlagunas.user.register.Success
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RegisterCoreTest {
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
    fun givenNewUser_whenRegister_thenSuccessAndNavigatesToLogin() {
        backend.respondTo("PUT", "/user/", ok())

        assertThat(register().awaitCount(1)).containsExactly(Success)
        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.ToLogin)
    }

    @Test
    fun givenNewUser_whenRegister_thenSendsUserWithoutAuthorization() {
        backend.respondTo("PUT", "/user/", ok())

        register().awaitCount(1)

        val request = backend.awaitRequest()
        assertThat(request.body).isEqualTo(
            """{"username":"anna","firstname":"Anna","lastname":"Puig",""" +
                """"email":"anna@example.com","profilePic":"https://example.com/anna.png","password":"secret"}""",
        )
        assertThat(request.header("Authorization")).isNull()
    }

    @Test
    fun givenExistingUsername_whenRegister_thenErrorAndNoNavigation() {
        backend.respondTo("PUT", "/user/", httpError(409))

        assertThat(register().awaitCount(1)).single().isInstanceOf(RegistrationError::class)
            .prop(RegistrationError::message).isEqualTo("HTTP 409 Client Error")
        assertThat(harness.navigator.calls.values).isEmpty()
    }

    @Test
    fun givenServerError_whenRegister_thenError() {
        backend.respondTo("PUT", "/user/", httpError(500))

        assertThat(register().awaitCount(1)).single().isInstanceOf(RegistrationError::class)
            .prop(RegistrationError::message).isEqualTo("HTTP 500 Server Error")
        assertThat(harness.navigator.calls.values).isEmpty()
    }

    @Test
    fun givenDisconnect_whenRegister_thenError() {
        backend.respondTo("PUT", "/user/", disconnect())

        assertThat(register().awaitCount(1)).single().isInstanceOf(RegistrationError::class)
        assertThat(harness.navigator.calls.values).isEmpty()
    }

    private fun register() =
        harness.registerViewModel().let { viewModel ->
            recordStates<RegistrationState>(viewModel.onRegistration).also {
                viewModel.register(User("anna", "Anna", "Puig", "anna@example.com", "https://example.com/anna.png", "secret"))
            }
        }
}
