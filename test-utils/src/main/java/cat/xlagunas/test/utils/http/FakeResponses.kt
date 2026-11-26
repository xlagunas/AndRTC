package cat.xlagunas.test.utils.http

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy

/** 200 with a JSON body read from [fixture], or an empty body. */
fun ok(fixture: String? = null): MockResponse =
    MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(fixture?.let(::fixture).orEmpty())

fun httpError(
    code: Int,
    body: String = "",
): MockResponse = MockResponse().setResponseCode(code).setBody(body)

/**
 * The connection drops before any response is sent, which the client sees as an IOException.
 * DISCONNECT_AFTER_REQUEST is used because MockWebServer ignores DISCONNECT_AT_START when it
 * comes from a dispatcher.
 */
fun disconnect(): MockResponse = MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)

/** 200 whose body is not valid JSON. */
fun malformedJson(): MockResponse =
    MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody("{\"broken\"")
