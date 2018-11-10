package cat.xlagunas.viv.push

import cat.xlagunas.domain.contact.ContactRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class PushMessageHandler : FirebaseMessagingService() {

    @Inject
    lateinit var contactRepository: ContactRepository

    @Inject
    lateinit var pushTokenPresenter: PushTokenPresenter

    private val disposables = CompositeDisposable()

    override fun onCreate() {
        // DaggerMonolythComponent.builder().withParentComponent(VivApplication.appComponent(this)).build()
        //    .inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("Message received from push. Message type: ${remoteMessage.data["eventType"]}")
        disposables += contactRepository
            .forceUpdate()
            .subscribe({}, { Timber.e(it, "Couldn't update contacts from push") })
    }

    override fun onNewToken(token: String?) {
        pushTokenPresenter.registerToken()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}