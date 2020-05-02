package cat.xlagunas.signaling.data

import io.socket.emitter.Emitter

interface SocketIOEmitterProvider {
    fun getEmitter(): Emitter

    fun getId(): String
}
