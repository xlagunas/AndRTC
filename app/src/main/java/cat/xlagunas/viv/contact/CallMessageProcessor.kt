package cat.xlagunas.viv.contact

import android.content.Context
import cat.xlagunas.push.CallMessage
import cat.xlagunas.push.Message
import cat.xlagunas.push.MessageProcessor
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