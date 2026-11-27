package cat.xlagunas.viv.push

import android.app.Application
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import cat.xlagunas.core.persistence.db.FriendEntity
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.LogRecorder
import cat.xlagunas.test.utils.http.FakeBackend
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
