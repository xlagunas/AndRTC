package cat.xlagunas.viv.push

import android.app.Application
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.LogRecorder
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.FakeResponse
import cat.xlagunas.test.utils.http.disconnect
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.ok
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Malformed JSON is not covered: PUT /token/ returns a Completable, so the response body is
// never parsed and a malformed body cannot change the outcome.
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class PushTokenRotationCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    @get:Rule
    val logs = LogRecorder()

    private lateinit var harness: PushCoreHarness

    private val provider get() = harness.pushTokenProvider

    @Before
    fun setUp() {
        harness = PushCoreHarness(backend, rule)
    }

    @After
    fun tearDown() {
        harness.close()
    }

    @Test
    fun givenLoggedInUser_whenNewToken_thenRegistersNewToken() {
        givenLoggedInWithRegisteredToken()
        backend.respondTo("PUT", "/token/", ok())

        harness.pushTokenPresenter.onNewToken(NEW_TOKEN)

        val request = backend.awaitRequest()
        assertThat(request.method).isEqualTo("PUT")
        assertThat(request.path).isEqualTo("/token/")
        assertThat(request.body).isEqualTo("""{"value":"$NEW_TOKEN","platform":"ANDROID"}""")
        assertThat(request.header("Authorization")).isEqualTo("token-123")
        // The first change is the setup's mark; the second one is the rotation's.
        assertThat(provider.registrationChanges.awaitCount(2)).containsExactly(true, true)
        assertThat(provider.invalidations).isEqualTo(0)
    }

    @Test
    fun givenUnauthorized_whenNewToken_thenNotRegisteredAndTokenKept() = assertRotationFailureKeepsToken(httpError(401))

    @Test
    fun givenForbidden_whenNewToken_thenNotRegisteredAndTokenKept() = assertRotationFailureKeepsToken(httpError(403))

    @Test
    fun givenNotFound_whenNewToken_thenNotRegisteredAndTokenKept() = assertRotationFailureKeepsToken(httpError(404))

    @Test
    fun givenServerError_whenNewToken_thenNotRegisteredAndTokenKept() = assertRotationFailureKeepsToken(httpError(500))

    // No request count assertion: OkHttp may retry a dropped connection.
    @Test
    fun givenDisconnect_whenNewToken_thenNotRegisteredAndTokenKept() = assertRotationFailureKeepsToken(disconnect())

    @Test
    fun givenLoggedOut_whenNewToken_thenNothingSent() {
        harness.pushTokenPresenter.onNewToken(NEW_TOKEN)

        // Requests go out on an OkHttp thread: wait for the guard to run before asserting absence.
        logs.awaitMessage(LOGGED_OUT_LOG)
        assertThat(backend.requests).isEmpty()
        assertThat(provider.invalidations).isEqualTo(0)
        assertThat(provider.registrationChanges.values).isEmpty()
    }

    private fun assertRotationFailureKeepsToken(response: FakeResponse) {
        givenLoggedInWithRegisteredToken()
        backend.respondTo("PUT", "/token/", response)

        harness.pushTokenPresenter.onNewToken(NEW_TOKEN)

        // The setup only records `true`, so the first `false` is the rotation's clear.
        provider.registrationChanges.awaitValue { !it }
        assertThat(provider.isTokenRegistered()).isFalse()
        assertThat(provider.invalidations).isEqualTo(0)
        assertThat(logs.awaitError().message).contains(ROTATION_ERROR)
    }

    private fun givenLoggedInWithRegisteredToken() {
        harness.givenLoggedInUser()
        provider.markTokenAsRegistered()
    }

    private companion object {
        const val NEW_TOKEN = "push-token-2"
        const val LOGGED_OUT_LOG = "New push token received while logged out"
        const val ROTATION_ERROR = "Error registering rotated push token"
    }
}
