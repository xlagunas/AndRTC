package cat.xlagunas.test.utils.http

/** 200 with a JSON body read from [fixture], or an empty body. */
fun ok(fixture: String? = null): FakeResponse = FakeResponse(code = 200, body = fixture?.let(::fixture).orEmpty(), json = true)

fun httpError(
    code: Int,
    body: String = "",
): FakeResponse = FakeResponse(code = code, body = body)

/**
 * The connection drops before any response is sent, which the client sees as an IOException.
 * OkHttp retries once on a fresh connection, so one call can produce two received requests.
 */
fun disconnect(): FakeResponse = FakeResponse(disconnect = true)

/** 200 whose body is not valid JSON. */
fun malformedJson(): FakeResponse = FakeResponse(code = 200, body = "{\"broken\"", json = true)

/** Keeps this response pending until [gate] is released. */
fun FakeResponse.heldBy(gate: ResponseGate): FakeResponse = copy(gate = gate)
