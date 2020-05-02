package cat.xlagunas.signaling.data.extensions

import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Emitter.on(eventType: String): Flow<String> = callbackFlow<String> {
    val listener = Emitter.Listener { message ->
        val stringMessage = message.map { it as String }
        offer(stringMessage.first())
    }

    on(eventType, listener)
    awaitClose { this@on.off(eventType, listener) }
}
