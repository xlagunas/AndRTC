package cat.xlagunas.test.utils

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.junit.Rule
import org.junit.Test
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

class LogRecorderTest {
    @get:Rule
    val logs = LogRecorder()

    @Test
    fun givenTimberError_whenLogged_thenAwaitErrorReturnsIt() {
        val cause = IllegalStateException("boom")

        Timber.e(cause, "something failed")

        val entry = logs.awaitError()
        assertThat(entry.message).isEqualTo("something failed")
        assertThat(entry.throwable).isEqualTo(cause)
    }

    @Test
    fun givenNoLog_whenAwaitMessage_thenFailsWithinTimeout() {
        assertFailure { logs.awaitMessage("never logged", timeout = 50.milliseconds) }
            .isInstanceOf(AssertionError::class)
    }

    @Test
    fun givenDebugAndError_whenAwaitMessage_thenFirstContainingTextReturned() {
        Timber.d("hello world")

        assertThat(logs.awaitMessage("world").message).isEqualTo("hello world")
    }
}
