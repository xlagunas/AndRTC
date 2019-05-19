package cat.xlagunas.contact.ui

import android.annotation.SuppressLint
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.push.Message
import cat.xlagunas.push.MessageProcessor
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