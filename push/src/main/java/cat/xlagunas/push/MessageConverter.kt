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
import timber.log.Timber
import javax.inject.Inject

class MessageConverter
    @Inject
    constructor() {
        fun toMessage(remoteMessage: RemoteMessage): cat.xlagunas.core.push.Message? {
            val messageKey = remoteMessage.data["eventType"] ?: return null

            return try {
                when (valueOf(messageKey)) {
                    CREATE_CALL -> {
                        val params = remoteMessage.data["params"]
                        if (params == null) {
                            Timber.w("Missing params on push eventType $messageKey")
                            null
                        } else {
                            CallMessage(CREATE_CALL, params)
                        }
                    }
                    ACCEPT_CALL, REJECT_CALL -> {
                        Timber.w("Unsupported push eventType $messageKey")
                        null
                    }
                    REQUEST_FRIENDSHIP ->
                        ContactMessage(
                            REQUEST_FRIENDSHIP,
                        )
                    ACCEPT_FRIENDSHIP ->
                        ContactMessage(
                            ACCEPT_FRIENDSHIP,
                        )
                    REJECT_FRIENDSHIP ->
                        ContactMessage(
                            REJECT_FRIENDSHIP,
                        )
                }
            } catch (ex: IllegalArgumentException) {
                Timber.e("Invalid eventType received on push notification $messageKey")
                null
            }
        }
    }
