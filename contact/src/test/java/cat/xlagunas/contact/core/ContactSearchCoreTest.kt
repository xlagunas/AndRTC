package cat.xlagunas.contact.core

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import assertk.assertions.single
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.FakeResponse
import cat.xlagunas.test.utils.http.disconnect
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.malformedJson
import cat.xlagunas.test.utils.http.ok
import cat.xlagunas.test.utils.recordStates
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactSearchCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    private lateinit var harness: ContactCoreHarness

    @Before
    fun setUp() {
        harness = ContactCoreHarness(backend, rule)
    }

    @After
    fun tearDown() {
        harness.close()
    }

    @Test
    fun givenMatches_whenFindContact_thenRequestSentAndResultsMapped() {
        harness.givenLoggedInUser()
        backend.respondTo("GET", "/contact/search/jordi", ok("contact/search_jordi.json"))

        val states = recordStates(harness.contactViewModel().findContact("jordi"))

        assertThat(states.awaitCount(1))
            .containsExactly(Result.success(listOf(Friend(12, "jordina", "Jordina", null, "jordina@example.com", "NONE"))))
        val request = backend.awaitRequest()
        assertThat(request.method).isEqualTo("GET")
        assertThat(request.path).isEqualTo("/contact/search/jordi")
        assertThat(request.header("Authorization")).isEqualTo("token-123")
    }

    @Test
    fun givenUnauthorized_whenFindContact_thenFailure() {
        givenFailingSearch(httpError(401))

        val states = recordStates(harness.contactViewModel().findContact("jordi"))

        assertThat(states.awaitCount(1).single().isFailure).isTrue()
    }

    @Test
    fun givenForbidden_whenFindContact_thenFailure() {
        givenFailingSearch(httpError(403))

        val states = recordStates(harness.contactViewModel().findContact("jordi"))

        assertThat(states.awaitCount(1).single().isFailure).isTrue()
    }

    @Test
    fun givenNotFound_whenFindContact_thenFailure() {
        givenFailingSearch(httpError(404))

        val states = recordStates(harness.contactViewModel().findContact("jordi"))

        assertThat(states.awaitCount(1).single().isFailure).isTrue()
    }

    @Test
    fun givenServerError_whenFindContact_thenFailure() {
        givenFailingSearch(httpError(500))

        val states = recordStates(harness.contactViewModel().findContact("jordi"))

        assertThat(states.awaitCount(1).single().isFailure).isTrue()
    }

    @Test
    fun givenDisconnect_whenFindContact_thenFailure() {
        givenFailingSearch(disconnect())

        val states = recordStates(harness.contactViewModel().findContact("jordi"))

        assertThat(states.awaitCount(1).single().isFailure).isTrue()
    }

    @Test
    fun givenMalformedJson_whenFindContact_thenFailure() {
        givenFailingSearch(malformedJson())

        val states = recordStates(harness.contactViewModel().findContact("jordi"))

        assertThat(states.awaitCount(1).single().isFailure).isTrue()
    }

    private fun givenFailingSearch(response: FakeResponse) {
        harness.givenLoggedInUser()
        backend.respondTo("GET", "/contact/search/jordi", response)
    }
}
