package cat.xlagunas.call

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _state = MutableLiveData<CallState>(CallState.Idle)
    val state: LiveData<CallState> = _state

    fun createCall(contacts: List<Long>) {
        if (_state.value == CallState.Creating) return
        _state.value = CallState.Creating
        viewModelScope.launch {
            // The confirmation dialog is dismissed (and this ViewModel cleared) as soon as the
            // user confirms, so the call must outlive viewModelScope cancellation
            withContext(NonCancellable) {
                val call =
                    try {
                        withContext(ioDispatcher) { callRepository.createCall(contacts) }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Timber.e(e, "Error creating call")
                        _state.value = CallState.Error
                        return@withContext
                    }
                try {
                    navigator.startCall(call.id)
                } catch (e: Exception) {
                    // The call already exists, so a Retry would create a second one: log and finish
                    Timber.e(e, "Error starting call ${call.id}")
                }
                _state.value = CallState.Started
            }
        }
    }
}
