package cat.xlagunas.push

import cat.xlagunas.core.push.MessageProcessor
import cat.xlagunas.core.push.MessageType
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

/** Converts an incoming push into a [cat.xlagunas.core.push.Message] and hands it to its processor. */
class PushMessageRouter
    @Inject
    constructor(
        private val messageConverter: MessageConverter,
        private val messageProcessors: Map<MessageType, @JvmSuppressWildcards Provider<MessageProcessor>>,
    ) {
        fun route(remoteMessage: RemoteMessage) {
            val message = messageConverter.toMessage(remoteMessage)
            if (message == null) {
                Timber.d("Invalid push message received, couldn't parse it.")
                return
            }
            messageProcessors[message.messageType]?.get()?.processMessage(message)
        }
    }
