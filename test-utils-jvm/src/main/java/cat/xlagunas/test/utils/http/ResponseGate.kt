package cat.xlagunas.test.utils.http

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

/**
 * Keeps a [FakeResponse] pending until the test calls [release]. Attach it with
 * [heldBy]; the request is recorded as soon as it arrives, so tests can observe the
 * in-flight state before releasing the answer.
 */
class ResponseGate {
    private val latch = CountDownLatch(1)

    /** Lets the held response go out. Safe to call more than once. */
    fun release() = latch.countDown()

    internal fun await(timeout: Duration): Boolean = latch.await(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
}
