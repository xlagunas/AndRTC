package cat.xlagunas.ws_messaging

import cat.xlagunas.ws_messaging.data.SessionAdapter
import cat.xlagunas.ws_messaging.data.UserSession
import cat.xlagunas.ws_messaging.model.AnswerMessage
import cat.xlagunas.ws_messaging.model.IceCandidateMessage
import cat.xlagunas.ws_messaging.model.Message
import cat.xlagunas.ws_messaging.model.MessageMapper
import cat.xlagunas.ws_messaging.model.OfferMessage
import cat.xlagunas.ws_messaging.model.Session
import cat.xlagunas.ws_messaging.model.SessionMapper
import com.google.gson.GsonBuilder
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

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
        val expectedOfferMessage = OfferMessage(fakeLocalSession, fakeOfferSessionDescription)
        val expectedAnswerMessage = AnswerMessage(fakeLocalSession, fakeAnswerSessionDescription)
        val expectedIceCandidateMessage = IceCandidateMessage(fakeLocalSession, fakeIceCandidate)

        val values = mutableListOf<Message>()

        webSocketController.joinConference("1234")
        val job = launch {
            webSocketController.receivedMessageChannel.asFlow()
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
            webSocketController.participantsChannel.asFlow()
                .collect { values.add(it) }
        }
        emitter.getEmitter().emit("NEW_USER", fakeLocalSession.getId())

        assertThat(values[0]).usingRecursiveComparison().isEqualTo(fakeLocalSession)
        job.cancel()
    }

    class FakeWebSocketEmitter : WebSocketEmitterProvider {
        private val fakeEmitter = Emitter()

        override fun getEmitter(): Emitter {
            return fakeEmitter
        }

        override fun getId(): String {
            return fakeLocalSession.getId()
        }
    }

    companion object {
        val fakeLocalSession: Session = UserSession("1234")
        val fakeOfferSessionDescription =
            SessionDescription(SessionDescription.Type.OFFER, "fake offer description")
        val fakeAnswerSessionDescription =
            SessionDescription(SessionDescription.Type.ANSWER, "fake answer description")
        val fakeIceCandidate =
            IceCandidate("fakeSdpMid", 1024, "fakeSdp for ice candidate string")

    }
}