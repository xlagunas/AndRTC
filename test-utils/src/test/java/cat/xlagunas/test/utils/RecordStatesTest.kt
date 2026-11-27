package cat.xlagunas.test.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class RecordStatesTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun givenLiveData_whenPostingValues_thenRecorded() {
        val liveData = MutableLiveData<String>()
        val recorder = recordStates(liveData)

        liveData.postValue("x")
        liveData.postValue("y")

        assertThat(recorder.values).containsExactly("x", "y")
    }

    @Test
    fun givenFlow_whenEmitting_thenRecorded() =
        runTest {
            val flow = MutableSharedFlow<Int>()
            val recorder = recordStates(flow, backgroundScope)

            flow.emit(1)
            flow.emit(2)

            assertThat(recorder.values).containsExactly(1, 2)
        }
}
