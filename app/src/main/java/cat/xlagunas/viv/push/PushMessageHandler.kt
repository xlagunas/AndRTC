package cat.xlagunas.viv.push

import cat.xlagunas.push.PushMessageRouter
import cat.xlagunas.push.PushTokenPresenter
import cat.xlagunas.viv.dagger.VivApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class PushMessageHandler : FirebaseMessagingService() {
    @Inject
    lateinit var pushTokenPresenter: PushTokenPresenter

    @Inject
    lateinit var pushMessageRouter: PushMessageRouter

    override fun onCreate() {
        VivApplication.appComponent(this).inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        pushMessageRouter.route(remoteMessage)
    }

    override fun onNewToken(token: String) = pushTokenPresenter.onNewToken(token)
}
