package cat.xlagunas.push.core

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import cat.xlagunas.core.network.HttpClientFactory
import cat.xlagunas.push.PushTokenApi
import cat.xlagunas.push.PushTokenRepositoryImpl
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.FakePushTokenProvider
import cat.xlagunas.test.utils.awaitResult
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.FakeResponse
import cat.xlagunas.test.utils.http.disconnect
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.ok
import com.google.gson.Gson
import org.junit.Rule
import org.junit.Test
import java.io.IOException

// Malformed JSON is not covered: addPushToken returns a Completable, so the response body
// is never parsed and a malformed body cannot change the outcome.
class PushTokenRepositoryCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    private val provider = FakePushTokenProvider()

    private val repository by lazy {
        PushTokenRepositoryImpl(
            provider,
            HttpClientFactory.retrofit(backend.url, HttpClientFactory.okHttpClient(emptyList()), Gson())
                .create(PushTokenApi::class.java),
            rule.rxSchedulers,
        )
    }

    @Test
    fun givenToken_whenRegisterPushToken_thenSendsTokenAndMarksRegistered() {
        backend.respondTo("PUT", "/token/", ok())

        val result = repository.registerPushToken().awaitResult()

        assertThat(result.isSuccess).isTrue()
        val request = backend.awaitRequest()
        assertThat(request.method).isEqualTo("PUT")
        assertThat(request.path).isEqualTo("/token/")
        assertThat(request.body).isEqualTo("""{"value":"push-token-1","platform":"ANDROID"}""")
        assertThat(provider.isTokenRegistered()).isTrue()
    }

    @Test
    fun givenUnauthorized_whenRegisterPushToken_thenFailsAndNotRegistered() = assertRegistrationFails(httpError(401), expectRequest = true)

    @Test
    fun givenForbidden_whenRegisterPushToken_thenFailsAndNotRegistered() = assertRegistrationFails(httpError(403), expectRequest = true)

    @Test
    fun givenNotFound_whenRegisterPushToken_thenFailsAndNotRegistered() = assertRegistrationFails(httpError(404), expectRequest = true)

    @Test
    fun givenServerError_whenRegisterPushToken_thenFailsAndNotRegistered() = assertRegistrationFails(httpError(500), expectRequest = true)

    @Test
    fun givenDisconnect_whenRegisterPushToken_thenFailsAndNotRegistered() = assertRegistrationFails(disconnect())

    @Test
    fun givenTokenUnavailable_whenRegisterPushToken_thenFailsWithoutRequest() {
        provider.failure = IOException("Play services unavailable")

        val result = repository.registerPushToken().awaitResult()

        assertThat(result.isFailure).isTrue()
        assertThat(backend.requests).isEmpty()
        assertThat(provider.isTokenRegistered()).isFalse()
    }

    @Test
    fun givenNewToken_whenRegisterPushTokenWithToken_thenSendsIt() {
        backend.respondTo("PUT", "/token/", ok())

        val result = repository.registerPushToken("push-token-2").awaitResult()

        assertThat(result.isSuccess).isTrue()
        val request = backend.awaitRequest()
        assertThat(request.method).isEqualTo("PUT")
        assertThat(request.path).isEqualTo("/token/")
        assertThat(request.body).isEqualTo("""{"value":"push-token-2","platform":"ANDROID"}""")
        assertThat(provider.registrationChanges.values).containsExactly(true)
        assertThat(provider.invalidations).isEqualTo(0)
    }

    @Test
    fun givenServerError_whenRegisterPushTokenWithToken_thenRegistrationCleared() {
        provider.markTokenAsRegistered()
        backend.respondTo("PUT", "/token/", httpError(500))

        val result = repository.registerPushToken("push-token-2").awaitResult()

        assertThat(result.isFailure).isTrue()
        assertThat(provider.registrationChanges.values).containsExactly(true, false)
        assertThat(provider.isTokenRegistered()).isFalse()
        assertThat(provider.invalidations).isEqualTo(0)
    }

    @Test
    fun givenRegisteredToken_whenInvalidatePushToken_thenProviderInvalidated() {
        provider.markTokenAsRegistered()

        val result = repository.invalidatePushToken().awaitResult()

        assertThat(result.isSuccess).isTrue()
        assertThat(provider.invalidations).isEqualTo(1)
    }

    // Does not assert the request count: OkHttp may retry after a disconnect.
    private fun assertRegistrationFails(
        response: FakeResponse,
        expectRequest: Boolean = false,
    ) {
        backend.respondTo("PUT", "/token/", response)

        val result = repository.registerPushToken().awaitResult()

        assertThat(result.isFailure).isTrue()
        if (expectRequest) assertThat(backend.awaitRequest().path).isEqualTo("/token/")
        assertThat(provider.isTokenRegistered()).isFalse()
    }
}
