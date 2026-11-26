package cat.xlagunas.test.utils

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Records every value the LiveData emits. Needs CoreTestRule or InstantTaskExecutorRule.
 */
fun <T> recordStates(source: LiveData<T>): StateRecorder<T> {
    val recorder = StateRecorder<T>()
    source.observeForever { recorder.record(it) }
    return recorder
}

/**
 * Records every value the Flow emits while [scope] is active.
 */
fun <T> recordStates(
    source: Flow<T>,
    scope: CoroutineScope,
): StateRecorder<T> {
    val recorder = StateRecorder<T>()
    scope.launch(start = CoroutineStart.UNDISPATCHED) {
        source.collect { recorder.record(it) }
    }
    return recorder
}
