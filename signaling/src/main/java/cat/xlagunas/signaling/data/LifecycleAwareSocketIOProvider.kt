package cat.xlagunas.signaling.data

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.socket.client.IO
import io.socket.emitter.Emitter
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject

class LifecycleAwareSocketIOProvider @Inject constructor(
    activity: AppCompatActivity,
    okHttpClient: OkHttpClient,
    webSocketUrl: HttpUrl
) : LifecycleObserver, SocketIOEmitterProvider {
    private val socket = IO.socket(webSocketUrl.toString())

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