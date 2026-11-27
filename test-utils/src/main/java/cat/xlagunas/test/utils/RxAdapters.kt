package cat.xlagunas.test.utils

import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Subscribes and waits (bounded) for completion; returns success or the error. */
fun Completable.awaitResult(timeout: Duration = 5.seconds): Result<Unit> {
    val latch = CountDownLatch(1)
    var result: Result<Unit>? = null
    val disposable =
        subscribe(
            {
                result = Result.success(Unit)
                latch.countDown()
            },
            {
                result = Result.failure(it)
                latch.countDown()
            },
        )
    try {
        if (!latch.await(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)) {
            throw AssertionError("Completable did not finish within $timeout")
        }
    } finally {
        disposable.dispose()
    }
    return result!!
}

/** Returns the first emitted value, failing the test on error, empty or timeout. */
fun <T : Any> Flowable<T>.awaitFirst(timeout: Duration = 5.seconds): T {
    val recorder = StateRecorder<Result<T>>()
    val disposable =
        firstElement().subscribe(
            { recorder.record(Result.success(it)) },
            { recorder.record(Result.failure(it)) },
            { recorder.record(Result.failure(AssertionError("Flowable completed without a value"))) },
        )
    try {
        return recorder.awaitCount(1, timeout).single().getOrThrow()
    } finally {
        disposable.dispose()
    }
}
