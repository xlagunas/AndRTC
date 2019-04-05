package cat.xlagunas.viv.push

import android.content.Context
import cat.xlagunas.viv.contact.ContactUtils
import timber.log.Timber
import javax.inject.Inject

class CallMessageProcessor @Inject constructor(
    private val context: Context
) :
    MessageProcessor {
    override fun processMessage(message: Message) {
        Timber.d("Processing Call push message for room $")
        val intent = ContactUtils.generateCallIntent((message as CallMessage).callId, context)
        context.startActivity(intent)
    }
}