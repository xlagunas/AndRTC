package cat.xlagunas.ws_messaging

import io.socket.emitter.Emitter

interface WebSocketEmitterProvider {
    fun getEmitter(): Emitter

    fun getId(): String
}