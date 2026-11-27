package cat.xlagunas.test.utils.http

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy

/** Engine adapter, not for tests: converts a MockWebServer request into a [ReceivedRequest]. */
fun RecordedRequest.toReceivedRequest() =
    ReceivedRequest(
        method = method.orEmpty(),
        path = requestUrl?.encodedPath.orEmpty(),
        query = requestUrl?.encodedQuery,
        headers = headers.toMultimap(),
        body = body.readUtf8(),
    )

/**
 * Engine adapter, not for tests: converts a [FakeResponse] into a MockWebServer response.
 * Uses DISCONNECT_AFTER_REQUEST because MockWebServer ignores DISCONNECT_AT_START when it
 * comes from a dispatcher.
 */
fun FakeResponse.toMockResponse(): MockResponse =
    if (disconnect) {
        MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)
    } else {
        MockResponse()
            .setResponseCode(code)
            .setBody(body)
            .apply { if (json) setHeader("Content-Type", "application/json") }
    }
