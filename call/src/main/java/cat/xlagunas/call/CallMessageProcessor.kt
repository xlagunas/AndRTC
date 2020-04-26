package cat.xlagunas.call

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cat.xlagunas.push.ChannelId
import cat.xlagunas.push.Message
import cat.xlagunas.push.MessageProcessor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import timber.log.Timber
import javax.inject.Inject

class CallMessageProcessor @Inject constructor(
    private val context: Context,
    private val callRepository: CallRepository,
    private val notificationManager: NotificationManagerCompat,
    @ChannelId private val channelId: String
) :
    MessageProcessor {
    @SuppressLint("CheckResult")
    override fun processMessage(message: Message) {
        Timber.d("Processing Call push message for room $")
        GlobalScope.launch {
            val call = callRepository.getCallDetails(message)
            startConference(call)
        }
    }

    private fun startConference(call: Call) {
        createPriorityNotification(call)
    }

    private fun createPriorityNotification(call: Call) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText("New Call received")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .addAction(generateAcceptAction(call))
            .build()
        notificationManager.notify(0, notification)
    }

    private fun generateAcceptAction(call: Call) = NotificationCompat.Action(
        android.R.drawable.ic_menu_call,
        "Accept",
        getConferenceIntent(call)
    )

    private fun getConferenceIntent(call: Call) = PendingIntent.getActivity(
        context,
        1000,
        generateRoomIntent(call),
        PendingIntent.FLAG_ONE_SHOT
    )

    private fun generateRoomIntent(call: Call): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(HttpUrl.get("https://viv.cat/conference?roomId=${call.id}").toString())
        )
        return intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
}