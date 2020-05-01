package cat.xlagunas.call

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cat.xlagunas.push.CallMessage
import cat.xlagunas.push.ChannelId
import cat.xlagunas.push.Message
import cat.xlagunas.push.MessageProcessor
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import timber.log.Timber
import javax.inject.Inject

class CallMessageProcessor @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    @ChannelId private val channelId: String,
    private val gson: Gson
) :
    MessageProcessor {
    @SuppressLint("CheckResult")
    override fun processMessage(message: Message) {
        GlobalScope.launch {
            val callId = extractRoomId((message as CallMessage).params)
            Timber.d("Processing Call push message for room $callId")
            startConference(callId)
        }
    }

    private fun startConference(callId: String) {
        createPriorityNotification(callId)
    }

    private fun createPriorityNotification(callId: String) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText(context.getString(R.string.notification_message))
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .addAction(generateAcceptAction(callId))
            .build()
        notificationManager.notify(0, notification)
    }

    private fun generateAcceptAction(callId: String) = NotificationCompat.Action(
        android.R.drawable.ic_menu_call,
        context.getString(R.string.notification_action_btn),
        getConferenceIntent(callId)
    )

    private fun getConferenceIntent(callId: String) = PendingIntent.getActivity(
        context,
        1000,
        generateRoomIntent(callId),
        PendingIntent.FLAG_ONE_SHOT
    )

    private fun generateRoomIntent(callId: String): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(HttpUrl.get("https://viv.cat/conference?roomId=${callId}").toString())
        )
        return intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    private fun extractRoomId(jsonString: String): String {
        val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            .getAsJsonObject("content")
        return jsonObject["callId"].asString
    }
}