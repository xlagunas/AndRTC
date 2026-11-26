package cat.xlagunas.call

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import cat.xlagunas.core.push.CallMessage
import cat.xlagunas.core.push.MessageType
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CallMessageProcessorTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val scope = TestScope(StandardTestDispatcher())

    private lateinit var processor: CallMessageProcessor

    @Before
    fun setUp() {
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Calls", NotificationManager.IMPORTANCE_HIGH),
        )
        processor =
            CallMessageProcessor(
                context,
                NotificationManagerCompat.from(context),
                CHANNEL_ID,
                Gson(),
                scope,
            )
    }

    @Test
    fun givenCallMessage_whenProcessedAndScopeNotAdvanced_thenNoNotificationYet() {
        processor.processMessage(callMessage())

        assertThat(shadowOf(notificationManager).allNotifications).isEmpty()
    }

    @Test
    fun givenCallMessage_whenProcessed_thenNotificationWithAcceptAction() {
        processor.processMessage(callMessage())

        scope.advanceUntilIdle()

        val notifications = shadowOf(notificationManager).allNotifications
        assertThat(notifications).hasSize(1)
        assertThat(notifications.single().actions.map { it.title.toString() })
            .containsExactly(context.getString(R.string.notification_action_btn))
    }

    private fun callMessage() = CallMessage(MessageType.CREATE_CALL, """{"content":{"callId":"room-1"}}""")

    private companion object {
        const val CHANNEL_ID = "test-channel"
    }
}
