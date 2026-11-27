package cat.xlagunas.viv.push

import android.app.Application
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEmpty
import cat.xlagunas.core.persistence.db.FriendEntity
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.LogRecorder
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.FakeResponse
import cat.xlagunas.test.utils.http.disconnect
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.malformedJson
import cat.xlagunas.test.utils.http.ok
import com.google.firebase.messaging.RemoteMessage
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class ContactsPushCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    @get:Rule
    val logs = LogRecorder()

    private lateinit var harness: PushCoreHarness

    @Before
    fun setUp() {
        harness = PushCoreHarness(backend, rule)
    }

    @After
    fun tearDown() {
        harness.close()
    }

    @Test
    fun givenFriendshipPush_whenContactListReturned_thenRoomUpdated() {
        harness.givenLoggedInUser()
        givenPreExistingFriend()
        backend.respondTo("GET", "/contact/list", ok("push/contact_list.json"))

        routeFriendshipPush()

        logs.awaitMessage("updating roster")
        assertThat(harness.friendsOf()).containsExactlyInAnyOrder("jordi", "marta")
    }

    @Test
    fun givenUnauthorized_whenFriendshipPush_thenErrorLoggedAndContactsKept() {
        assertRefreshFailureKeepsContacts(httpError(401))
    }

    @Test
    fun givenForbidden_whenFriendshipPush_thenErrorLoggedAndContactsKept() {
        assertRefreshFailureKeepsContacts(httpError(403))
    }

    @Test
    fun givenNotFound_whenFriendshipPush_thenErrorLoggedAndContactsKept() {
        assertRefreshFailureKeepsContacts(httpError(404))
    }

    @Test
    fun givenServerError_whenFriendshipPush_thenErrorLoggedAndContactsKept() {
        assertRefreshFailureKeepsContacts(httpError(500))
    }

    @Test
    fun givenDisconnect_whenFriendshipPush_thenErrorLoggedAndContactsKept() {
        // No request count assertion: OkHttp may retry a dropped connection.
        assertRefreshFailureKeepsContacts(disconnect())
    }

    @Test
    fun givenMalformedContactList_whenFriendshipPush_thenErrorLoggedAndContactsKept() {
        assertRefreshFailureKeepsContacts(malformedJson())
    }

    @Test
    fun givenLoggedOut_whenFriendshipPush_thenErrorLoggedAndNoCrash() {
        backend.respondTo("GET", "/contact/list", ok("push/contact_list.json"))

        routeFriendshipPush()

        logs.awaitError()
        assertThat(harness.friendsOf()).isEmpty()
    }

    private fun assertRefreshFailureKeepsContacts(response: FakeResponse) {
        harness.givenLoggedInUser()
        givenPreExistingFriend()
        backend.respondTo("GET", "/contact/list", response)

        routeFriendshipPush()

        logs.awaitError()
        assertThat(harness.friendsOf()).containsExactly(PRE_EXISTING_FRIEND)
    }

    private fun givenPreExistingFriend() {
        harness.database.friendDao().insert(
            FriendEntity(99, 3, PRE_EXISTING_FRIEND, "Pre Existing", "pre@example.com", "", "FRIEND"),
        )
    }

    private fun routeFriendshipPush() {
        harness.router.route(RemoteMessage.Builder("test@fcm").setData(mapOf("eventType" to "REQUEST_FRIENDSHIP")).build())
    }

    private companion object {
        const val PRE_EXISTING_FRIEND = "friend99"
    }
}
