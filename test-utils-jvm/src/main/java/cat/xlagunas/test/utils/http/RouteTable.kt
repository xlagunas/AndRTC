package cat.xlagunas.test.utils.http

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Engine-independent routing: maps a method and path to a [FakeResponse]. Shared by
 * [FakeBackend] and the standalone e2e backend.
 */
class RouteTable {
    private class Route(val method: String, val path: Regex, val response: FakeResponse)

    private val routes = CopyOnWriteArrayList<Route>()

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

    /** The response of the latest matching route, or null when no route matches. */
    fun match(
        method: String,
        path: String,
    ): FakeResponse? = routes.firstOrNull { it.method == method && it.path.matches(path) }?.response

    /** Removes every route. */
    fun clear() {
        routes.clear()
    }
}
