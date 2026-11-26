package cat.xlagunas.test.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cat.xlagunas.core.scheduler.RxSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Owns everything asynchronous in a core test: the coroutine main dispatcher, the Rx
 * schedulers and the LiveData executor. When RxJava goes, only [rxSchedulers] goes with it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoreTestRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestRule {
    val rxSchedulers =
        RxSchedulers(
            Schedulers.trampoline(),
            Schedulers.trampoline(),
            Schedulers.trampoline(),
        )

    /** Scope to inject where production uses the application scope. */
    val testScope = TestScope(testDispatcher)

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement {
        val withMainDispatcher =
            object : Statement() {
                override fun evaluate() {
                    Dispatchers.setMain(testDispatcher)
                    try {
                        base.evaluate()
                    } finally {
                        Dispatchers.resetMain()
                    }
                }
            }
        return InstantTaskExecutorRule().apply(withMainDispatcher, description)
    }
}
