package cat.xlagunas.conference.data

import cat.xlagunas.conference.domain.WsMessagingApi
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.ShutdownReason
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test

class ConferenceRepositoryImpTest {

    private lateinit var wsMessagingApi: WsMessagingApi
    private lateinit var scarletInstance: Scarlet

    private val lifecycleRegistry = LifecycleRegistry()

    @Before
    fun setUp() {
        val httpClient = OkHttpClient.Builder().build()
        scarletInstance = Scarlet.Builder()
            .webSocketFactory(httpClient.newWebSocketFactory("ws://localhost:8080/testRoom/testUser"))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .build()
    }

    @Test
    fun joinRoom() {
        wsMessagingApi = scarletInstance.create(WsMessagingApi::class.java)
        val helloMsg = Message("JOIN", "", "")
        lifecycleRegistry.onNext(Lifecycle.State.Stopped.WithReason(ShutdownReason.GRACEFUL))

        var message: Message

        wsMessagingApi.sendMessage(helloMsg)
        lifecycleRegistry.onNext(Lifecycle.State.Stopped.AndAborted)
        val routine = GlobalScope.async {
            message = wsMessagingApi.observeMessageStream().receive()
            assert(message.data != null)
        }
    }

    @Test
    fun getConnectedUsers() {
    }
}