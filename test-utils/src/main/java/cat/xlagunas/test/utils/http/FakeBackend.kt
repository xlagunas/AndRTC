package cat.xlagunas.test.utils.http

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Fake HTTP backend for tests: a MockWebServer whose responses are chosen by method and
 * path, never by request order. A request with no matching route gets a 404 and fails the
 * test once its body has run.
 */
class FakeBackend : TestRule {
    private class Route(val method: String, val path: Regex, val response: MockResponse)

    private val server = MockWebServer()
    private val routes = CopyOnWriteArrayList<Route>()
    private val received = CopyOnWriteArrayList<RecordedRequest>()
    private val unmatched = CopyOnWriteArrayList<String>()

    init {
        server.dispatcher = RoutingDispatcher()
    }

    /**
     * Base URL of the server, without a trailing slash. Safe to read before the rule runs
     * (e.g. in a property initializer): reading it starts the server.
     */
    val url: String
        get() = server.url("/").toString().removeSuffix("/")

    /** Requests received so far, in arrival order. */
    val requests: List<RecordedRequest>
        get() = received.toList()

    /**
     * Answers [method] requests whose path (without query) fully matches the [path] regex.
     * [path] is a raw regex, so escape literal `.` or `+`. The latest registration for the
     * same method and path wins.
     */
    fun respondTo(
        method: String,
        path: String,
        response: MockResponse,
    ) {
        routes.add(0, Route(method.uppercase(), Regex(path), response))
    }

    /** Takes the next request in arrival order, failing if none arrives within [timeout]. */
    fun awaitRequest(timeout: Duration = DEFAULT_TIMEOUT): RecordedRequest =
        server.takeRequest(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            ?: throw AssertionError("No request received within $timeout")

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        object : Statement() {
            override fun evaluate() {
                // MockWebServer starts itself on first use; url() forces it without double-starting.
                server.url("/")
                val failure =
                    try {
                        base.evaluate()
                        null
                    } catch (t: Throwable) {
                        t
                    } finally {
                        server.shutdown()
                    }
                val unmatchedReport =
                    unmatched.takeIf { it.isNotEmpty() }
                        ?.let { AssertionError("Requests with no fake response: ${it.joinToString()}") }
                when {
                    failure != null -> throw failure.apply { unmatchedReport?.let(::addSuppressed) }
                    unmatchedReport != null -> throw unmatchedReport
                }
            }
        }

    private inner class RoutingDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            received.add(request)
            val method = request.method.orEmpty()
            val path = request.requestUrl?.encodedPath.orEmpty()
            val route = routes.firstOrNull { it.method == method && it.path.matches(path) }
            if (route != null) return route.response
            unmatched.add("$method $path")
            return MockResponse().setResponseCode(404).setBody("No fake response for $method $path")
        }
    }

    companion object {
        val DEFAULT_TIMEOUT = 5.seconds
    }
}
