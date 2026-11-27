package cat.xlagunas.test.utils.http

/** Reads `fixtures/<path>` from the test-utils resources. */
fun fixture(path: String): String =
    FakeBackend::class.java.getResource("/fixtures/$path")?.readText()
        ?: throw IllegalArgumentException("Fixture not found: $path")
