package cat.xlagunas.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.xlagunas.core.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallViewModel @Inject constructor(
    private val callRepository: CallRepository,
    private val navigator: Navigator
) : ViewModel() {

    fun createCall(contacts: List<Long>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO + NonCancellable) {
                val call = callRepository.createCall(contacts)
                navigator.startCall(call.id)
            }
        }
    }
}