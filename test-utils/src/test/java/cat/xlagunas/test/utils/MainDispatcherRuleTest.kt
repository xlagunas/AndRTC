package cat.xlagunas.test.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRuleTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenMainDispatcherRule_whenLaunchOnMain_thenRunsOnTestDispatcher() {
        var ran = false

        CoroutineScope(Dispatchers.Main).launch { ran = true }

        assertThat(ran).isTrue()
    }

    @Test
    fun givenStandardTestDispatcher_whenLaunchOnMain_thenRunsOnlyAfterAdvance() {
        val dispatcher = StandardTestDispatcher()
        val rule = MainDispatcherRule(dispatcher)
        var ran = false
        var ranBeforeAdvance = true

        val statement =
            object : Statement() {
                override fun evaluate() {
                    CoroutineScope(Dispatchers.Main).launch { ran = true }
                    ranBeforeAdvance = ran
                    dispatcher.scheduler.advanceUntilIdle()
                }
            }
        rule.apply(statement, Description.EMPTY).evaluate()

        assertThat(ranBeforeAdvance).isFalse()
        assertThat(ran).isTrue()
    }
}
