package cat.xlagunas.viv.push

import android.content.Context
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.viv.commons.di.VivApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class PushMessageHandler : FirebaseMessagingService() {

    @Inject
    lateinit var contactRepository: ContactRepository
    val disposables = CompositeDisposable()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        (applicationContext as VivApplication).applicationComponent.inject(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("Message received from push. Message type: ${remoteMessage.data["eventType"]}")
        val disposable = contactRepository
            .forceUpdate()
            .subscribe({}, { Timber.e(it, "Couldn't update contacts from push") })

        disposables.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}