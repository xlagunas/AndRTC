package cat.xlagunas.test.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Rule
import org.junit.Test
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.milliseconds

class StateRecorderTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun givenRecordedValues_whenAwaitCount_thenReturnsThemInOrder() {
        val recorder = StateRecorder<Int>()
        recorder.record(1)
        recorder.record(2)

        assertThat(recorder.awaitCount(2)).containsExactly(1, 2)
    }

    @Test
    fun givenValuesFromAnotherThread_whenAwaitCount_thenReturnsOnceAvailable() {
        val recorder = StateRecorder<Int>()

        thread { listOf(1, 2, 3).forEach(recorder::record) }

        assertThat(recorder.awaitCount(3)).containsExactly(1, 2, 3)
    }

    @Test
    fun givenTooFewValues_whenAwaitCount_thenFailsListingRecordedValues() {
        val recorder = StateRecorder<String>()
        recorder.record("a")

        assertThatThrownBy { recorder.awaitCount(2, 100.milliseconds) }
            .isInstanceOf(AssertionError::class.java)
            .hasMessageContaining("[a]")
    }

    @Test
    fun givenMatchingValue_whenAwaitValue_thenReturnsIt() {
        val recorder = StateRecorder<Int>()

        thread { listOf(1, 2, 3).forEach(recorder::record) }

        assertThat(recorder.awaitValue { it > 1 }).isEqualTo(2)
    }

    @Test
    fun givenConcurrentRecords_whenReading_thenNoValueLost() {
        val recorder = StateRecorder<Int>()

        val writers = (0 until 4).map { thread { repeat(1000) { recorder.record(it) } } }
        while (writers.any { it.isAlive }) {
            recorder.values.size
        }

        assertThat(recorder.awaitCount(4000)).hasSize(4000)
    }

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
