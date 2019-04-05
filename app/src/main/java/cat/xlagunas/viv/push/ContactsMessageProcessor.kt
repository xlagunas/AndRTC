package cat.xlagunas.viv.push

import android.annotation.SuppressLint
import cat.xlagunas.domain.contact.ContactRepository
import timber.log.Timber
import javax.inject.Inject

class ContactsMessageProcessor @Inject constructor(private val contactRepository: ContactRepository) :
    MessageProcessor {

    @SuppressLint("CheckResult")
    override fun processMessage(message: Message) {
        contactRepository.forceUpdate()
            .subscribe { Timber.d("Push notification received, updating roster") }
    }
}