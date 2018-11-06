package cat.xlagunas.conference.data

import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.Conferencee
import cat.xlagunas.conference.domain.WsMessagingApi
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import io.reactivex.Flowable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class ConferenceRepositoryImp : ConferenceRepository {

    //TODO Dagger2 inject through it
    private val lifecycleRegistry = LifecycleRegistry()
    private lateinit var wsMessagingApi: WsMessagingApi

    override fun joinRoom(roomId: String) {
        wsMessagingApi = Scarlet.Builder()
            .webSocketFactory(OkHttpClient.Builder().build().newWebSocketFactory("ws://localhost:8080/testRoom/testUser"))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .lifecycle(lifecycleRegistry)
            .build().create(WsMessagingApi::class.java)

        GlobalScope.launch {
            val users = wsMessagingApi.getRoomParticipants()
            for (user in users) {
                wsMessagingApi.sendMessage(Message("testUser", "Hello from Android", user.userId))
            }
        }

        lifecycleRegistry.onNext(Lifecycle.State.Started)
    }

    override fun getConnectedUsers(): Flowable<Conferencee> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logoutRoom() {
        lifecycleRegistry.onNext(Lifecycle.State.Stopped.WithReason())
    }
}