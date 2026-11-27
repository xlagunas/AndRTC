package cat.xlagunas.viv.push

import android.app.Application
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import cat.xlagunas.core.push.MessageType
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
class PushRoutingCoreTest {
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
    fun givenRequestFriendshipPush_whenRouted_thenContactsRefreshed() {
        assertFriendshipPushRefreshesContacts(MessageType.REQUEST_FRIENDSHIP)
    }

    @Test
    fun givenAcceptFriendshipPush_whenRouted_thenContactsRefreshed() {
        assertFriendshipPushRefreshesContacts(MessageType.ACCEPT_FRIENDSHIP)
    }

    @Test
    fun givenRejectFriendshipPush_whenRouted_thenContactsRefreshed() {
        assertFriendshipPushRefreshesContacts(MessageType.REJECT_FRIENDSHIP)
    }

    @Test
    fun givenCreateCallPush_whenRouted_thenCallNotificationPosted() {
        harness.router.route(
            remoteMessage(mapOf("eventType" to "CREATE_CALL", "params" to """{"content":{"callId":"room-1"}}""")),
        )

        assertThat(harness.notifications()).hasSize(1)
        assertThat(backend.requests).isEmpty()
    }

    @Test
    fun givenPushWithoutEventType_whenRouted_thenIgnored() {
        assertIgnored(remoteMessage(mapOf("params" to "{}")))
    }

    @Test
    fun givenUnknownEventType_whenRouted_thenIgnored() {
        assertIgnored(remoteMessage(mapOf("eventType" to "SOMETHING_ELSE")))
        // Event types are case-sensitive.
        assertIgnored(remoteMessage(mapOf("eventType" to "create_call", "params" to """{"content":{"callId":"room-1"}}""")))
    }

    private fun assertFriendshipPushRefreshesContacts(type: MessageType) {
        harness.givenLoggedInUser()
        backend.respondTo("GET", "/contact/list", ok("push/contact_list.json"))

        harness.router.route(remoteMessage(mapOf("eventType" to type.name)))

        val request = backend.awaitRequest()
        assertThat(request.method).isEqualTo("GET")
        assertThat(request.path).isEqualTo("/contact/list")
        assertThat(request.header("Authorization")).isEqualTo("token-123")
        logs.awaitMessage("updating roster")
        assertThat(harness.friendsOf()).containsExactlyInAnyOrder("jordi", "marta")
    }

    private fun assertIgnored(message: RemoteMessage) {
        harness.router.route(message)

        assertThat(backend.requests).isEmpty()
        assertThat(harness.notifications()).isEmpty()
    }

    private fun remoteMessage(data: Map<String, String>) = RemoteMessage.Builder("test@fcm").setData(data).build()
}
