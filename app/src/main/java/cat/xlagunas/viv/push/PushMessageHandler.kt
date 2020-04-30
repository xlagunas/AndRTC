package cat.xlagunas.viv.push

import cat.xlagunas.push.MessageConverter
import cat.xlagunas.push.MessageProcessor
import cat.xlagunas.push.MessageType
import cat.xlagunas.push.PushTokenPresenter
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.VivApplication
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class PushMessageHandler : FirebaseMessagingService() {

    @Inject
    lateinit var pushTokenPresenter: PushTokenPresenter

    @Inject
    lateinit var messageProcessors: Map<MessageType, @JvmSuppressWildcards Provider<MessageProcessor>>

    @Inject
    lateinit var messageConverter: MessageConverter

    override fun onCreate() {
        // TODO Maybe move it to a factory approach mimicing viewmodelprovider
        VivApplication.appComponent(this).inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val message = messageConverter.toMessage(remoteMessage)
        if (message == null) {
            Timber.d("Invalid push message received, couldn't parse it.")
            return
        }
        messageProcessors[message.messageType]?.get()?.processMessage(message)
    }

    override fun onNewToken(token: String?) {
        if (pushTokenPresenter.isPushTokenRegistered()) {
            pushTokenPresenter.clearPushToken()
        }
    }
}