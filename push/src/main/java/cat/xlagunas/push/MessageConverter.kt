package cat.xlagunas.push

import cat.xlagunas.core.push.CallMessage
import cat.xlagunas.core.push.ContactMessage
import cat.xlagunas.core.push.MessageType.ACCEPT_CALL
import cat.xlagunas.core.push.MessageType.ACCEPT_FRIENDSHIP
import cat.xlagunas.core.push.MessageType.CREATE_CALL
import cat.xlagunas.core.push.MessageType.REJECT_CALL
import cat.xlagunas.core.push.MessageType.REJECT_FRIENDSHIP
import cat.xlagunas.core.push.MessageType.REQUEST_FRIENDSHIP
import cat.xlagunas.core.push.MessageType.valueOf
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject
import timber.log.Timber

class MessageConverter @Inject constructor() {

    fun toMessage(remoteMessage: RemoteMessage): cat.xlagunas.core.push.Message? {
        val messageKey = remoteMessage.data["eventType"] ?: return null

        return try {
            when (valueOf(messageKey)) {
                CREATE_CALL -> CallMessage(
                    CREATE_CALL,
                    remoteMessage.data["params"]!!
                )
                ACCEPT_CALL -> TODO("Accept call flow still not implemented")
                REJECT_CALL -> TODO("Reject call flow still not implemented")
                REQUEST_FRIENDSHIP -> ContactMessage(
                    REQUEST_FRIENDSHIP
                )
                ACCEPT_FRIENDSHIP -> ContactMessage(
                    ACCEPT_FRIENDSHIP
                )
                REJECT_FRIENDSHIP -> ContactMessage(
                    REJECT_FRIENDSHIP
                )
            }
        } catch (ex: IllegalArgumentException) {
            Timber.e("Invalid eventType received on push notification $messageKey")
            null
        }
    }
}
