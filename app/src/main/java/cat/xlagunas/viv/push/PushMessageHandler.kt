package cat.xlagunas.viv.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception

class PushMessageHandler: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
    }

    override fun onMessageSent(p0: String?) {
        super.onMessageSent(p0)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onSendError(p0: String?, p1: Exception?) {
        super.onSendError(p0, p1)
    }

}