package cat.xlagunas.viv.push

import android.content.Context
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.viv.commons.di.VivApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import javax.inject.Inject

class PushMessageHandler : FirebaseMessagingService() {

    @Inject
    lateinit var contactRepository: ContactRepository

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        (applicationContext as VivApplication).applicationComponent.inject(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("Message received from push. Message type: ${remoteMessage.data["eventType"]}")
        contactRepository.forceUpdate()
    }
}