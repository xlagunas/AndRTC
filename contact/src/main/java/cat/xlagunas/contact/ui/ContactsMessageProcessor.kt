package cat.xlagunas.contact.ui

import android.annotation.SuppressLint
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.core.push.Message
import cat.xlagunas.core.push.MessageProcessor
import timber.log.Timber
import javax.inject.Inject

class ContactsMessageProcessor @Inject constructor(private val contactRepository: ContactRepository) :
    cat.xlagunas.core.push.MessageProcessor {

    @SuppressLint("CheckResult")
    override fun processMessage(message: cat.xlagunas.core.push.Message) {
        contactRepository.forceUpdate()
            .subscribe { Timber.d("Push notification received, updating roster") }
    }
}