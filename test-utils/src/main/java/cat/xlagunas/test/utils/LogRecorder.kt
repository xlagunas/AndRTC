package cat.xlagunas.test.utils

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.time.Duration

/** Records Timber logs during a test so tests can await expected (error) logs. */
class LogRecorder : TestRule {
    data class LogEntry(
        val priority: Int,
        val message: String,
        val throwable: Throwable?,
    )

    val entries = StateRecorder<LogEntry>()

    private val tree =
        object : Timber.Tree() {
            override fun log(
                priority: Int,
                tag: String?,
                message: String,
                t: Throwable?,
            ) {
                entries.record(LogEntry(priority, message.withoutStackTrace(t), t))
            }
        }

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        object : Statement() {
            override fun evaluate() {
                Timber.plant(tree)
                try {
                    base.evaluate()
                } finally {
                    Timber.uproot(tree)
                }
            }
        }

    fun awaitError(timeout: Duration = StateRecorder.DEFAULT_TIMEOUT): LogEntry = entries.awaitValue(timeout) { it.priority >= WARN }

    fun awaitMessage(
        text: String,
        timeout: Duration = StateRecorder.DEFAULT_TIMEOUT,
    ): LogEntry = entries.awaitValue(timeout) { it.message.contains(text) }

    /** Timber appends the stack trace to the message when a throwable is logged; strip it. */
    private fun String.withoutStackTrace(t: Throwable?): String {
        if (t == null) return this
        val trace = StringWriter().also { t.printStackTrace(PrintWriter(it)) }.toString()
        return removeSuffix("\n$trace").removeSuffix("\n" + trace.trimEnd())
    }

    private companion object {
        /** Same value as android.util.Log.WARN, avoiding the android.jar stub in JVM tests. */
        const val WARN = 5
    }
}
