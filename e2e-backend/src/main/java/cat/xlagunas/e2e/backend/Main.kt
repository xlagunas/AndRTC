package cat.xlagunas.e2e.backend

import java.net.BindException
import java.util.concurrent.CountDownLatch
import kotlin.system.exitProcess

private const val DEFAULT_PORT = 8484

fun main() {
    val port = System.getenv("E2E_BACKEND_PORT")?.toIntOrNull() ?: DEFAULT_PORT
    val backend = E2eBackend()
    try {
        backend.start(port)
    } catch (e: BindException) {
        System.err.println("Port $port is in use")
        exitProcess(1)
    }
    println("e2e-backend listening on ${backend.url}")
    CountDownLatch(1).await()
}
