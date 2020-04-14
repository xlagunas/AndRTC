package cat.xlagunas.ws_messaging

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.socket.client.IO
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject

class LifecycleAwareWebSocketProvider @Inject constructor(
    activity: AppCompatActivity,
    okHttpClient: OkHttpClient
) : LifecycleObserver, WebSocketEmitterProvider {
    //TODO MOVE URL TO FLAVOR
    private val socket = IO.socket("https://wss.viv.cat")
    // private val socket = IO.socket("http://192.168.1.139:3000")

    init {
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
        IO.setDefaultOkHttpCallFactory(okHttpClient)
        activity.lifecycle.addObserver(this)
    }

    override fun getEmitter(): Emitter {
        return socket
    }

    override fun getId(): String {
        return socket.id()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        if (!socket.connected()) {
            socket.connect()
            Timber.d("Connecting Socket.IO instance")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        socket.disconnect()
        Timber.d("Disconnecting Socket.IO instance")
    }
}