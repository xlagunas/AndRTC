package cat.xlagunas.test.utils

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Thread-safe list of emitted values that tests can wait on.
 *
 * Values may be recorded from any thread (OkHttp, Rx, coroutines). Waits are bounded and
 * fail with the values recorded so far.
 */
class StateRecorder<T> {
    private val lock = ReentrantLock()
    private val changed = lock.newCondition()
    private val recorded = mutableListOf<T>()

    val values: List<T>
        get() = lock.withLock { recorded.toList() }

    fun record(value: T) {
        lock.withLock {
            recorded.add(value)
            changed.signalAll()
        }
    }

    fun awaitCount(
        count: Int,
        timeout: Duration = DEFAULT_TIMEOUT,
    ): List<T> =
        awaitUntil(timeout, { "Expected at least $count values within $timeout but got $it" }) {
            if (recorded.size >= count) recorded.take(count) else null
        }

    fun awaitValue(
        timeout: Duration = DEFAULT_TIMEOUT,
        predicate: (T) -> Boolean,
    ): T =
        awaitUntil(timeout, { "No value matched within $timeout; got $it" }) {
            recorded.firstOrNull(predicate)
        }

    private fun <R> awaitUntil(
        timeout: Duration,
        failure: (List<T>) -> String,
        result: () -> R?,
    ): R =
        lock.withLock {
            var remaining = timeout.inWholeNanoseconds
            var found = result()
            while (found == null && remaining > 0) {
                remaining = changed.awaitNanos(remaining)
                found = result()
            }
            found ?: throw AssertionError(failure(recorded.toList()))
        }

    companion object {
        val DEFAULT_TIMEOUT = 5.seconds
    }
}
