package cat.xlagunas.e2e.backend

import cat.xlagunas.test.utils.http.ReceivedRequest
import cat.xlagunas.test.utils.http.RouteTable
import cat.xlagunas.test.utils.http.toMockResponse
import cat.xlagunas.test.utils.http.toReceivedRequest
import com.google.gson.GsonBuilder
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.net.InetAddress
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Fake HTTP backend for E2E flows. Routes are selected by scenario through
 * `POST /__scenario/{name}`, which also resets the request log; `GET /__requests` returns the
 * non-control requests received since then and `GET /__health` answers `ok`.
 */
class E2eBackend(private val scenarios: Map<String, (RouteTable) -> Unit> = Scenarios.all) {
    private val server = MockWebServer()
    private val routes = RouteTable()
    private val log = CopyOnWriteArrayList<ReceivedRequest>()
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        server.dispatcher = ControlDispatcher()
    }

    /** Base URL without a trailing slash. */
    val url: String
        get() = server.url("/").toString().removeSuffix("/")

    fun start(port: Int) = server.start(InetAddress.getByName("127.0.0.1"), port)

    fun shutdown() = server.shutdown()

    private fun select(name: String): MockResponse {
        val scenario =
            scenarios[name]
                ?: return MockResponse().setResponseCode(404)
                    .setBody("Unknown scenario '$name'. Known: ${scenarios.keys.sorted().joinToString()}")
        synchronized(this) {
            routes.clear()
            log.clear()
            scenario(routes)
        }
        return MockResponse().setResponseCode(200).setBody("ok")
    }

    private fun requestLog(): MockResponse {
        val entries =
            log.map { mapOf("method" to it.method, "path" to it.path, "query" to it.query, "body" to it.body) }
        return MockResponse().setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody(gson.toJson(entries))
    }

    private inner class ControlDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val received = request.toReceivedRequest()
            val path = received.path
            return when {
                path == "/__health" -> MockResponse().setResponseCode(200).setBody("ok")
                path == "/__requests" -> requestLog()
                path.startsWith("/__scenario/") && received.method == "POST" -> select(path.removePrefix("/__scenario/"))
                else -> {
                    log.add(received)
                    routes.match(received.method, path)?.toMockResponse()
                        ?: MockResponse().setResponseCode(404).setBody("No route for ${received.method} $path")
                }
            }
        }
    }
}
