package cat.xlagunas.test.utils.http

/**
 * A canned backend answer. Tests only see this type, so the engine behind [FakeBackend]
 * (MockWebServer today, Ktor MockEngine after the KMP move) can change without touching them.
 */
data class FakeResponse(
    val code: Int = 200,
    val body: String = "",
    val json: Boolean = false,
    val disconnect: Boolean = false,
    val gate: ResponseGate? = null,
)

/** A request the fake backend received, independent of the HTTP engine. */
data class ReceivedRequest(
    val method: String,
    val path: String,
    val query: String?,
    val headers: Map<String, List<String>>,
    val body: String,
) {
    /** First value of the header, matched case-insensitively. */
    fun header(name: String): String? = headers.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value?.firstOrNull()
}
