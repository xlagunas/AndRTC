package cat.xlagunas.e2e.backend

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import cat.xlagunas.test.utils.http.fixture
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Before
import org.junit.Test

class E2eBackendTest {
    private val backend = E2eBackend()
    private val client = OkHttpClient()

    @Before
    fun setUp() = backend.start(0)

    @After
    fun tearDown() = backend.shutdown()

    private fun call(
        method: String,
        path: String,
        body: String? = null,
    ): Pair<Int, String> {
        val requestBody = body?.toRequestBody("application/json".toMediaType()) ?: if (method == "GET") null else "".toRequestBody()
        val request = Request.Builder().url(backend.url + path).method(method, requestBody).build()
        client.newCall(request).execute().use { return it.code to it.body!!.string() }
    }

    private fun requestLog() = JsonParser.parseString(call("GET", "/__requests").second).asJsonArray

    @Test
    fun givenUnknownScenario_whenSelected_then404ListingKnownNames() {
        val (code, body) = call("POST", "/__scenario/nope")

        assertThat(code).isEqualTo(404)
        assertThat(body).contains("logged_in_contacts")
    }

    @Test
    fun givenLoggedInContacts_whenContactListRequested_thenFixtureBody() {
        call("POST", "/__scenario/logged_in_contacts")

        val (code, body) = call("GET", "/contact/list")

        assertThat(code).isEqualTo(200)
        assertThat(body).isEqualTo(fixture("contact/contact_list.json"))
    }

    @Test
    fun givenRequests_whenRequestLogRead_thenListsThemInOrderWithoutControlCalls() {
        call("POST", "/__scenario/logged_in_contacts")
        call("PUT", "/token/", """{"token":"t"}""")

        val log = requestLog()

        assertThat(log.size()).isEqualTo(1)
        val entry = log[0].asJsonObject
        assertThat(entry["method"].asString).isEqualTo("PUT")
        assertThat(entry["path"].asString).isEqualTo("/token/")
        assertThat(entry["body"].asString).isEqualTo("""{"token":"t"}""")
    }

    @Test
    fun givenRequests_whenScenarioSelected_thenLogIsEmpty() {
        call("POST", "/__scenario/logged_in_contacts")
        call("GET", "/user/")

        call("POST", "/__scenario/empty")

        assertThat(requestLog().map { it.toString() }).isEmpty()
    }

    @Test
    fun givenNoRoute_whenRequested_then404AndLogged() {
        call("POST", "/__scenario/empty")

        val (code, _) = call("GET", "/user/")

        assertThat(code).isEqualTo(404)
        assertThat(requestLog().map { it.asJsonObject["path"].asString }).containsExactly("/user/")
    }

    @Test
    fun givenRunning_whenHealthRequested_thenOk() {
        val (code, body) = call("GET", "/__health")

        assertThat(code).isEqualTo(200)
        assertThat(body).isEqualTo("ok")
    }

    @Test
    fun givenLoggedInContacts_whenAcceptRoutePut_then200() {
        call("POST", "/__scenario/logged_in_contacts")

        assertThat(call("PUT", "/contact/7").first).isEqualTo(200)
        assertThat(call("POST", "/contact/7/accept").first).isEqualTo(200)
        assertThat(call("POST", "/contact/7/reject").first).isEqualTo(200)
    }

    @Test
    fun givenLoggedInContacts_whenCallCreated_thenCallFixture() {
        call("POST", "/__scenario/logged_in_contacts")

        val (code, body) = call("PUT", "/call/", "{}")

        assertThat(code).isEqualTo(200)
        assertThat(body).isEqualTo(fixture("call/call_created.json"))
    }

    @Test
    fun givenLoggedInContacts_whenSearching_thenSearchFixture() {
        call("POST", "/__scenario/logged_in_contacts")

        val (code, body) = call("GET", "/contact/search/jordi")

        assertThat(code).isEqualTo(200)
        assertThat(body).isEqualTo(fixture("contact/search_jordi.json"))
    }

    @Test
    fun givenRegisterScenario_whenUserPut_then200() {
        call("POST", "/__scenario/register")

        assertThat(call("PUT", "/user/", "{}").first).isEqualTo(200)
        assertThat(call("GET", "/contact/list").second).isEqualTo(fixture("contact/contact_list_empty.json"))
    }

    @Test
    fun givenLoginRejected_whenAuth_then401() {
        call("POST", "/__scenario/login_rejected")

        assertThat(call("POST", "/auth/", "{}").first).isEqualTo(401)
    }

    @Test
    fun givenContactsEmpty_whenListRequested_thenEmptyArray() {
        call("POST", "/__scenario/contacts_empty")

        val (code, body) = call("GET", "/contact/list")

        assertThat(code).isEqualTo(200)
        assertThat(body).isEqualTo(fixture("contact/contact_list_empty.json"))
        assertThat(call("POST", "/auth/", "{}").first).isEqualTo(200)
    }
}
