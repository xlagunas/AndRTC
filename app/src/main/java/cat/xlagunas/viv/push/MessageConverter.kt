package cat.xlagunas.push

import cat.xlagunas.push.MessageType.ACCEPT_CALL
import cat.xlagunas.push.MessageType.ACCEPT_FRIENDSHIP
import cat.xlagunas.push.MessageType.CREATE_CALL
import cat.xlagunas.push.MessageType.REJECT_CALL
import cat.xlagunas.push.MessageType.REJECT_FRIENDSHIP
import cat.xlagunas.push.MessageType.REQUEST_FRIENDSHIP
import cat.xlagunas.push.MessageType.valueOf
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import javax.inject.Inject

class MessageConverter @Inject constructor() {

    fun toMessage(remoteMessage: RemoteMessage): Message? {
        val messageKey = remoteMessage.data["eventType"] ?: return null

        return try {
            val messageType = valueOf(messageKey)
            when (messageType) {
                CREATE_CALL -> CallMessage(CREATE_CALL, remoteMessage.data["params"]!!)
                ACCEPT_CALL -> TODO("Accept call flow still not implemented")
                REJECT_CALL -> TODO("Reject call flow still not implemented")
                REQUEST_FRIENDSHIP -> ContactMessage(REQUEST_FRIENDSHIP)
                ACCEPT_FRIENDSHIP -> ContactMessage(ACCEPT_FRIENDSHIP)
                REJECT_FRIENDSHIP -> ContactMessage(REJECT_FRIENDSHIP)
            }
        } catch (ex: IllegalArgumentException) {
            Timber.e("Invalid eventType received on push notification $messageKey")
            null
        }
    }
}