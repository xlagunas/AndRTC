package cat.xlagunas.test.utils

import androidx.lifecycle.MutableLiveData
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.concurrent.thread

class CoreTestRuleTest {
    @get:Rule
    val rule = CoreTestRule()

    @Test
    fun givenRule_whenRunning_thenMainDispatcherIsTestDispatcher() =
        runTest(rule.testDispatcher) {
            var ranOnMain = false

            withContext(Dispatchers.Main) { ranOnMain = true }

            assertThat(ranOnMain).isTrue()
        }

    @Test
    fun givenRule_whenRunning_thenLiveDataSetValueWorksOffMainThread() {
        val liveData = MutableLiveData<String>()
        var error: Throwable? = null

        thread { runCatching { liveData.value = "x" }.onFailure { error = it } }.join()

        assertThat(error).isNull()
        assertThat(liveData.value).isEqualTo("x")
    }

    @Test
    fun givenRule_thenRxSchedulersAreTrampoline() {
        assertThat(rule.rxSchedulers.io).isSameAs(Schedulers.trampoline())
        assertThat(rule.rxSchedulers.mainThread).isSameAs(Schedulers.trampoline())
        assertThat(rule.rxSchedulers.computation).isSameAs(Schedulers.trampoline())
    }

    @Test
    fun givenFailingTest_whenRuleFinishes_thenMainDispatcherReset() {
        val failing =
            object : Statement() {
                override fun evaluate() = throw IllegalStateException("boom")
            }

        assertThatThrownBy { CoreTestRule().apply(failing, Description.EMPTY).evaluate() }
            .hasMessage("boom")
        assertThatThrownBy { runBlocking { withContext(Dispatchers.Main) {} } }
            .isNotNull()
    }
}
