// package cat.xlagunas.conference.data
//
// import cat.xlagunas.conference.data.WsMessagingApi
// import com.tinder.scarlet.Lifecycle
// import com.tinder.scarlet.Scarlet
// import com.tinder.scarlet.ShutdownReason
// import com.tinder.scarlet.lifecycle.LifecycleRegistry
// import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
// import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
// import kotlinx.coroutines.runBlocking
// import okhttp3.OkHttpClient
// import org.assertj.core.api.Assertions.assertThat
// import org.junit.Before
// import org.junit.Ignore
// import org.junit.Test
//
// class ConferenceRepositoryImpTest {
//
//    private lateinit var wsMessagingApi: WsMessagingApi
//    private lateinit var scarletInstance: Scarlet
//
//    private val lifecycleRegistry = LifecycleRegistry()
//
//    @Before
//    fun setUp() {
//        val httpClient = OkHttpClient.Builder().build()
//        scarletInstance = Scarlet.Builder()
//            .webSocketFactory(httpClient.newWebSocketFactory("ws://localhost:8080/testRoom/testUser"))
//            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
//            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
//            .build()
//    }
//
//    @Test
//    fun joinRoom() {
//        wsMessagingApi = scarletInstance.create(WsMessagingApi::class.java)
//        val helloMsg = MessageDto("JOIN", "", MessageType.ICE_CANDIDATE, "")
//        lifecycleRegistry.onNext(Lifecycle.State.Stopped.WithReason(ShutdownReason.GRACEFUL))
//
//        wsMessagingApi.sendMessage(helloMsg)
//        lifecycleRegistry.onNext(Lifecycle.State.Stopped.AndAborted)
//        runBlocking {
//            val iceCandidateMessage = wsMessagingApi.observeIceCandidateStream().receive()
//            assertThat(iceCandidateMessage).isNotNull()
//        }
//    }
//
//    @Test
//    fun onNewUser() {
//    }
// }