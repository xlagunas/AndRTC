package cat.xlagunas.test.utils.http

import cat.xlagunas.test.utils.StateRecorder
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Fake HTTP backend for tests, whose responses are chosen by method and path, never by
 * request order. A request with no matching route gets a 404 and fails the test once its
 * body has run.
 *
 * Tests only see [FakeResponse] and [ReceivedRequest]; MockWebServer is an implementation
 * detail that can be swapped for Ktor's MockEngine after the KMP move.
 */
class FakeBackend(private val gateTimeout: Duration = DEFAULT_TIMEOUT) : TestRule {
    private class Route(val method: String, val path: Regex, val response: FakeResponse)

    private val server = MockWebServer()
    private val routes = CopyOnWriteArrayList<Route>()
    private val received = StateRecorder<ReceivedRequest>()
    private val awaited = AtomicInteger(0)
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

    /** Every request received so far, in arrival order. Not affected by [awaitRequest]. */
    val requests: List<ReceivedRequest>
        get() = received.values

    /**
     * Answers [method] requests whose path (without query) fully matches the [path] regex.
     * [path] is a raw regex, so escape literal `.` or `+`. The latest registration for the
     * same method and path wins.
     */
    fun respondTo(
        method: String,
        path: String,
        response: FakeResponse,
    ) {
        routes.add(0, Route(method.uppercase(), Regex(path), response))
    }

    /**
     * Returns the next request not yet returned by this method, in arrival order, failing if
     * none arrives within [timeout].
     */
    fun awaitRequest(timeout: Duration = DEFAULT_TIMEOUT): ReceivedRequest {
        val index = awaited.getAndIncrement()
        return received.awaitCount(index + 1, timeout)[index]
    }

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
            val receivedRequest = request.toReceivedRequest()
            received.record(receivedRequest)
            val method = receivedRequest.method
            val path = receivedRequest.path
            val route = routes.firstOrNull { it.method == method && it.path.matches(path) }
            if (route != null) {
                val gate = route.response.gate
                if (gate != null && !gate.await(gateTimeout)) {
                    unmatched.add("$method $path (gate never released)")
                    return MockResponse().setResponseCode(500).setBody("Gate never released for $method $path")
                }
                return route.response.toMockResponse()
            }
            unmatched.add("$method $path")
            return MockResponse().setResponseCode(404).setBody("No fake response for $method $path")
        }
    }

    companion object {
        val DEFAULT_TIMEOUT = 5.seconds
    }
}

private fun RecordedRequest.toReceivedRequest() =
    ReceivedRequest(
        method = method.orEmpty(),
        path = requestUrl?.encodedPath.orEmpty(),
        query = requestUrl?.encodedQuery,
        headers = headers.toMultimap(),
        body = body.readUtf8(),
    )

// DISCONNECT_AFTER_REQUEST: MockWebServer ignores DISCONNECT_AT_START when it comes from a dispatcher.
private fun FakeResponse.toMockResponse(): MockResponse =
    if (disconnect) {
        MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)
    } else {
        MockResponse()
            .setResponseCode(code)
            .setBody(body)
            .apply { if (json) setHeader("Content-Type", "application/json") }
    }
