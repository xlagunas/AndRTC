package cat.xlagunas.signaling.controller

import cat.xlagunas.signaling.TestUtils.fakeAnswerSessionMessage
import cat.xlagunas.signaling.TestUtils.fakeIceCandidateMessage
import cat.xlagunas.signaling.TestUtils.fakeLocalSession
import cat.xlagunas.signaling.TestUtils.fakeOfferSessionMessage
import cat.xlagunas.signaling.data.SessionAdapter
import cat.xlagunas.signaling.data.SocketIOEmitterProvider
import cat.xlagunas.signaling.data.mapper.MessageMapper
import cat.xlagunas.signaling.data.mapper.SessionMapper
import cat.xlagunas.signaling.domain.Message
import cat.xlagunas.signaling.domain.Session
import com.google.gson.GsonBuilder
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class SocketIOControllerTest {

    private val emitter =
        FakeSocketIOEmitter()
    private val gson =
        GsonBuilder().registerTypeAdapter(Session::class.java, SessionAdapter()).create()
    private val messageMapper =
        MessageMapper(gson)
    private val webSocketController =
        SocketIOController(
            messageMapper,
            SessionMapper(gson),
            emitter
        )

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

    class FakeSocketIOEmitter :
        SocketIOEmitterProvider {
        private val fakeEmitter = Emitter()

        override fun getEmitter(): Emitter {
            return fakeEmitter
        }

        override fun getId(): String {
            return fakeLocalSession().getId()
        }
    }
}
