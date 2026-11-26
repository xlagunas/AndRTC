package cat.xlagunas.test.utils.http

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

class FakeBackendTest {
    @get:Rule
    val backend = FakeBackend()

    private val client = OkHttpClient()

    @Test
    fun givenStubbedRoute_whenRequested_thenReturnsFixture() {
        backend.respondTo("GET", "/selftest/", ok("selftest/hello.json"))

        get(backend, "/selftest/").use {
            assertThat(it.code).isEqualTo(200)
            assertThat(it.body!!.string().trim()).isEqualTo("""{"value":"hello"}""")
        }
    }

    @Test
    fun givenRegexRoute_whenRequestedWithId_thenMatches() {
        backend.respondTo("POST", "/contact/\\d+/accept", ok())

        val request =
            Request.Builder().url(backend.url + "/contact/42/accept").post("".toRequestBody()).build()

        client.newCall(request).execute().use { assertThat(it.code).isEqualTo(200) }
    }

    @Test
    fun givenQueryString_whenRequested_thenMatchesPathOnly() {
        backend.respondTo("GET", "/contact/search/anna", ok())

        get(backend, "/contact/search/anna?x=1").use { assertThat(it.code).isEqualTo(200) }
    }

    @Test
    fun givenTwoRegistrations_whenRequested_thenLatestWins() {
        backend.respondTo("GET", "/user/", httpError(500))
        backend.respondTo("GET", "/user/", ok())

        get(backend, "/user/").use { assertThat(it.code).isEqualTo(200) }
    }

    @Test
    fun givenUnstubbedRequest_whenRequested_thenNotFoundAndRuleFails() {
        val other = FakeBackend()
        var body = ""
        val statement =
            object : Statement() {
                override fun evaluate() {
                    get(other, "/nope/").use {
                        assertThat(it.code).isEqualTo(404)
                        body = it.body!!.string()
                    }
                }
            }

        assertThatThrownBy { other.apply(statement, Description.EMPTY).evaluate() }
            .isInstanceOf(AssertionError::class.java)
            .hasMessageContaining("GET /nope/")
        assertThat(body).contains("GET /nope/")
    }

    @Test
    fun givenUrlReadBeforeRuleRuns_whenTestRuns_thenServerAnswers() {
        val other = FakeBackend()
        val urlBeforeStart = other.url
        var code = 0
        val statement =
            object : Statement() {
                override fun evaluate() {
                    other.respondTo("GET", "/user/", ok())
                    get(other, "/user/").use { code = it.code }
                }
            }

        other.apply(statement, Description.EMPTY).evaluate()

        assertThat(code).isEqualTo(200)
        assertThat(urlBeforeStart).isEqualTo(other.url)
    }

    @Test
    fun givenUnstubbedRequestAndFailingBody_whenRuleFinishes_thenUnmatchedReportedAsSuppressed() {
        val other = FakeBackend()
        val statement =
            object : Statement() {
                override fun evaluate() {
                    get(other, "/nope/").close()
                    throw IllegalStateException("body failed")
                }
            }

        val thrown = runCatching { other.apply(statement, Description.EMPTY).evaluate() }.exceptionOrNull()

        assertThat(thrown).isInstanceOf(IllegalStateException::class.java).hasMessage("body failed")
        assertThat(thrown!!.suppressed.map { it.message }).anyMatch { it!!.contains("GET /nope/") }
    }

    @Test
    fun givenRequestMade_whenAwaitRequest_thenReturnsIt() {
        backend.respondTo("GET", "/user/", ok())

        get(backend, "/user/").close()

        val recorded: ReceivedRequest = backend.awaitRequest()
        assertThat(recorded.method).isEqualTo("GET")
        assertThat(recorded.path).isEqualTo("/user/")
        assertThat(backend.requests).containsExactly(recorded)
    }

    @Test
    fun givenPostWithHeadersAndQuery_whenReceived_thenExposedWithoutOkHttpTypes() {
        backend.respondTo("POST", "/auth/", ok())

        val request =
            Request.Builder()
                .url(backend.url + "/auth/?source=test")
                .header("Authorization", "token-1")
                .post("""{"user":"anna"}""".toRequestBody())
                .build()
        client.newCall(request).execute().close()

        val recorded = backend.awaitRequest()
        assertThat(recorded.path).isEqualTo("/auth/")
        assertThat(recorded.query).isEqualTo("source=test")
        assertThat(recorded.header("authorization")).isEqualTo("token-1")
        assertThat(recorded.body).isEqualTo("""{"user":"anna"}""")
    }

    @Test
    fun givenTwoRequests_whenAwaitingTwice_thenReturnedInOrderAndRequestsKeepsBoth() {
        backend.respondTo("GET", "/user/", ok())
        backend.respondTo("GET", "/contact/list", ok())

        get(backend, "/user/").close()
        get(backend, "/contact/list").close()

        assertThat(backend.awaitRequest().path).isEqualTo("/user/")
        assertThat(backend.awaitRequest().path).isEqualTo("/contact/list")
        assertThat(backend.requests.map { it.path }).containsExactly("/user/", "/contact/list")
    }

    @Test
    fun givenCannedResponses_thenDescribeThemselves() {
        assertThat(ok()).isEqualTo(FakeResponse(code = 200, body = "", json = true))
        assertThat(httpError(401)).isEqualTo(FakeResponse(code = 401))
        assertThat(disconnect().disconnect).isTrue()
        assertThat(malformedJson().body).isEqualTo("{\"broken\"")
    }

    @Test
    fun givenNoRequest_whenAwaitRequest_thenFailsAfterTimeout() {
        assertThatThrownBy { backend.awaitRequest(100.milliseconds) }
            .isInstanceOf(AssertionError::class.java)
    }

    @Test
    fun givenDisconnect_whenRequested_thenIOException() {
        backend.respondTo("GET", "/user/", disconnect())

        assertThatThrownBy { get(backend, "/user/") }.isInstanceOf(IOException::class.java)
    }

    @Test
    fun givenMalformedJson_whenRequested_thenBodyIsNotValidJson() {
        backend.respondTo("GET", "/user/", malformedJson())

        get(backend, "/user/").use { assertThat(it.body!!.string()).isEqualTo("{\"broken\"") }
    }

    @Test
    fun givenMissingFixture_whenLoading_thenFailsWithPath() {
        assertThatThrownBy { fixture("missing/file.json") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Fixture not found: missing/file.json")
    }

    private fun get(
        target: FakeBackend,
        path: String,
    ): Response = client.newCall(Request.Builder().url(target.url + path).build()).execute()
}
