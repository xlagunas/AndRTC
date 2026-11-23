package cat.xlagunas.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.xlagunas.core.navigation.Navigator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CallViewModel internal constructor(
    private val callRepository: CallRepository,
    private val navigator: Navigator,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    @Inject
    constructor(
        callRepository: CallRepository,
        navigator: Navigator,
    ) : this(callRepository, navigator, Dispatchers.IO)

    fun createCall(contacts: List<Long>) {
        viewModelScope.launch {
            // The confirmation dialog is dismissed (and this ViewModel cleared) as soon as the
            // user confirms, so the call must outlive viewModelScope cancellation
            withContext(NonCancellable) {
                try {
                    val call = withContext(ioDispatcher) { callRepository.createCall(contacts) }
                    navigator.startCall(call.id)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Timber.e(e, "Error creating call")
                }
            }
        }
    }
}
