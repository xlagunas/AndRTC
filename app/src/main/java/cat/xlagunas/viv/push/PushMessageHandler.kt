package cat.xlagunas.viv.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class PushMessageHandler : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("Message received from push. Message type: ${remoteMessage.messageType}")
    }
}