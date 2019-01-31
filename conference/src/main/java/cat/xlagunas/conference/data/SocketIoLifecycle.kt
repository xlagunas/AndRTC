package cat.xlagunas.conference.data

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.socket.client.Socket
import timber.log.Timber
import javax.inject.Inject

class SocketIoLifecycle private constructor(private val activity: AppCompatActivity, private val socket: Socket, private val roomId: String) : LifecycleObserver {

    fun init(){
        Timber.d("Binding Socket.IO lifecycle to activity")
        activity.lifecycle.addObserver(this)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        if (!socket.connected()) {
            socket.connect()
            socket.emit("JOIN_ROOM", roomId)
            Timber.d("Connecting Socket.IO instance")
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        socket.disconnect()
        Timber.d("Disconnecting Socket.IO instance")
    }


    class Factory @Inject constructor(private val activity: AppCompatActivity, private val roomId: String){
        fun create(socket: Socket): SocketIoLifecycle{
            return SocketIoLifecycle(activity, socket, roomId)
        }
    }
}