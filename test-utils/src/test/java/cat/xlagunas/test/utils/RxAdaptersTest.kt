package cat.xlagunas.test.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Test

class RxAdaptersTest {
    @Test
    fun givenFailingCompletable_whenAwaitResult_thenFailure() {
        val error = IllegalStateException("nope")

        val result = Completable.error(error).awaitResult()

        assertThat(result.exceptionOrNull()).isEqualTo(error)
    }

    @Test
    fun givenCompletingCompletable_whenAwaitResult_thenSuccess() {
        val result = Completable.complete().awaitResult()

        assertThat(result).isSuccess()
    }

    @Test
    fun givenFlowable_whenAwaitFirst_thenFirstValue() {
        assertThat(Flowable.just(1, 2, 3).awaitFirst()).isEqualTo(1)
    }
}
