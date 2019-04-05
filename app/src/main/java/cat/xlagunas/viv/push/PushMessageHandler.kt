package cat.xlagunas.viv.push

import cat.xlagunas.core.di.VivApplication
import cat.xlagunas.push.MessageConverter
import cat.xlagunas.push.MessageProcessor
import cat.xlagunas.push.MessageType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.DaggerMonolythComponent
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
        DaggerMonolythComponent.builder().withParentComponent(VivApplication.appComponent(this))
            .build()
            .inject(this)
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