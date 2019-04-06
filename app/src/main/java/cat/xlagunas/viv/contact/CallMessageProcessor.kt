package cat.xlagunas.viv.contact

import android.annotation.SuppressLint
import android.content.Context
import cat.xlagunas.domain.call.Call
import cat.xlagunas.domain.call.CallRepository
import cat.xlagunas.push.Message
import cat.xlagunas.push.MessageProcessor
import timber.log.Timber
import javax.inject.Inject

class CallMessageProcessor @Inject constructor(
    private val context: Context,
    private val callRepository: CallRepository
) :
    MessageProcessor {
    @SuppressLint("CheckResult")
    override fun processMessage(message: Message) {
        Timber.d("Processing Call push message for room $")
        callRepository.getCallDetails(message)
            .subscribe(this::startConference) { Timber.e("Error processing call: ${it.message}") }
    }

    private fun startConference(call: Call) {
        val intent = ContactUtils.generateCallIntent(call.id, context)
        context.startActivity(intent)
    }
}