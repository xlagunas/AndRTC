package cat.xlagunas.contact.core

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import cat.xlagunas.contact.data.ContactCacheImpl.Companion.MAX_VALID_CACHE_MILLIS
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.ok
import cat.xlagunas.test.utils.recordStates
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactListCoreTest {
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
    fun givenExpiredCache_whenObservingContacts_thenFetchesStoresAndEmitsFriends() {
        harness.givenLoggedInUser()
        backend.respondTo("GET", "/contact/list", ok("contact/contact_list.json"))

        val states = recordStates(harness.contactViewModel().contacts)

        assertThat(states.awaitCount(1)).containsExactly(Result.success(listOf(jordi, marta)))
        val request = backend.awaitRequest()
        assertThat(request.method).isEqualTo("GET")
        assertThat(request.path).isEqualTo("/contact/list")
        assertThat(request.header("Authorization")).isEqualTo("token-123")
        assertThat(harness.storedFriends(3).map { it.friendId to it.relationShipStatus })
            .containsExactly(10L to "ACCEPTED", 11L to "PENDING")
        assertThat(harness.cacheTimestamp()).isEqualTo(harness.time.now)
    }

    @Test
    fun givenValidCache_whenObservingContacts_thenEmitsStoredFriendsWithoutRequest() {
        harness.givenLoggedInUser()
        harness.givenStoredFriends(3, jordi.toEntity(3))
        harness.givenValidCache()

        val states = recordStates(harness.contactViewModel().contacts)

        assertThat(states.awaitCount(1)).containsExactly(Result.success(listOf(jordi)))
        assertThat(backend.requests).isEmpty()
    }

    @Test
    fun givenCacheExactlyOneHourOld_whenObservingContacts_thenNoRequest() {
        harness.givenLoggedInUser()
        harness.givenStoredFriends(3, jordi.toEntity(3))
        harness.givenCacheTimestamp(harness.time.now - MAX_VALID_CACHE_MILLIS)

        val states = recordStates(harness.contactViewModel().contacts)

        assertThat(states.awaitCount(1)).containsExactly(Result.success(listOf(jordi)))
        assertThat(backend.requests).isEmpty()
    }

    @Test
    fun givenCacheOneHourAndOneMsOld_whenObservingContacts_thenRefreshes() {
        harness.givenLoggedInUser()
        harness.givenStoredFriends(3, jordi.toEntity(3))
        harness.givenCacheTimestamp(harness.time.now - MAX_VALID_CACHE_MILLIS - 1)
        backend.respondTo("GET", "/contact/list", ok("contact/contact_list.json"))

        val states = recordStates(harness.contactViewModel().contacts)

        assertThat(states.awaitCount(1)).containsExactly(Result.success(listOf(jordi, marta)))
        assertThat(backend.requests.map { it.path }).containsExactly("/contact/list")
    }
}
