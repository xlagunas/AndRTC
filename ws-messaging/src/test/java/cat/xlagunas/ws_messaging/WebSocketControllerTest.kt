package cat.xlagunas.ws_messaging

import cat.xlagunas.ws_messaging.TestUtils.fakeAnswerSessionMessage
import cat.xlagunas.ws_messaging.TestUtils.fakeIceCandidateMessage
import cat.xlagunas.ws_messaging.TestUtils.fakeLocalSession
import cat.xlagunas.ws_messaging.TestUtils.fakeOfferSessionMessage
import cat.xlagunas.ws_messaging.data.SessionAdapter
import cat.xlagunas.ws_messaging.model.Message
import cat.xlagunas.ws_messaging.model.MessageMapper
import cat.xlagunas.ws_messaging.model.Session
import cat.xlagunas.ws_messaging.model.SessionMapper
import com.google.gson.GsonBuilder
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class WebSocketControllerTest {

    private val emitter = FakeWebSocketEmitter()
    private val gson =
        GsonBuilder().registerTypeAdapter(Session::class.java, SessionAdapter()).create()
    private val messageMapper = MessageMapper(gson)
    private val webSocketController =
        WebSocketController(messageMapper, SessionMapper(gson), emitter)

    @Before
    fun setUp() {
    }

    @Test
    fun getReceivedMessageChannel() = runBlockingTest {
        val expectedOfferMessage = fakeOfferSessionMessage()
        val expectedAnswerMessage = fakeAnswerSessionMessage()
        val expectedIceCandidateMessage = fakeIceCandidateMessage()

        val values = mutableListOf<Message>()

        webSocketController.joinConference("1234")
        val job = launch {
            webSocketController.observeDirectMessages()
                .collect { values.add(it) }
        }
        webSocketController.sendMessage(expectedOfferMessage)
        webSocketController.sendMessage(expectedAnswerMessage)
        webSocketController.sendMessage(expectedIceCandidateMessage)

        assertThat(values[0]).usingRecursiveComparison().isEqualTo(expectedOfferMessage)
        assertThat(values[1]).usingRecursiveComparison().isEqualTo(expectedAnswerMessage)
        assertThat(values[2]).usingRecursiveComparison().isEqualTo(expectedIceCandidateMessage)
        job.cancel()
    }

    @Test
    fun getParticipantsChannel() = runBlockingTest {
        webSocketController.joinConference("1234")
        val values = mutableListOf<Session>()

        val job = launch {
            webSocketController.observeParticipants()
                .collect { values.add(it) }
        }
        emitter.getEmitter().emit("NEW_USER", fakeLocalSession().getId())

        assertThat(values[0]).usingRecursiveComparison().isEqualTo(fakeLocalSession())
        job.cancel()
    }

    class FakeWebSocketEmitter : WebSocketEmitterProvider {
        private val fakeEmitter = Emitter()

        override fun getEmitter(): Emitter {
            return fakeEmitter
        }

        override fun getId(): String {
            return fakeLocalSession().getId()
        }
    }
}