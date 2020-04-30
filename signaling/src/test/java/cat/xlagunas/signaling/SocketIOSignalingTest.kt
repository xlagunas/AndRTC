package cat.xlagunas.signaling

import cat.xlagunas.signaling.TestUtils.fakeAnswerSessionMessage
import cat.xlagunas.signaling.TestUtils.fakeIceCandidateMessage
import cat.xlagunas.signaling.TestUtils.fakeLocalSession
import cat.xlagunas.signaling.TestUtils.fakeOfferSessionMessage
import cat.xlagunas.signaling.data.SessionAdapter
import cat.xlagunas.signaling.domain.AnswerMessage
import cat.xlagunas.signaling.domain.IceCandidateMessage
import cat.xlagunas.signaling.data.mapper.MessageMapper
import cat.xlagunas.signaling.domain.OfferMessage
import cat.xlagunas.signaling.domain.Session
import cat.xlagunas.signaling.data.mapper.SessionMapper
import cat.xlagunas.signaling.controller.SocketIOController
import cat.xlagunas.signaling.controller.SocketIOControllerTest
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.Test

class SocketIOSignalingTest {
    private val gson =
        GsonBuilder().registerTypeAdapter(Session::class.java, SessionAdapter()).create()
    private val messageMapper =
        MessageMapper(gson)
    private val fakeEmitter =
        SocketIOControllerTest.FakeSocketIOEmitter()
    private val webSocketController =
        SocketIOController(
            messageMapper,
            SessionMapper(gson),
            fakeEmitter
        )
    private val signalingProtocol =
        SocketIOSignaling(webSocketController)

    @Test
    fun onNewSession() = runBlockingTest {
        val values = mutableListOf<Session>()

        val job = launch {
            signalingProtocol.onNewSession()
                .collect { values.add(it) }
        }
        fakeEmitter.getEmitter().emit("NEW_USER", fakeLocalSession().getId())

        Assertions.assertThat(values[0])
            .usingRecursiveComparison().isEqualTo(fakeLocalSession())
        job.cancel()
    }

    @Test
    fun onReceiveOffer() = runBlockingTest {
        val values = mutableListOf<OfferMessage>()
        val expectedOfferMessage = fakeOfferSessionMessage()
        val expectedAnswerMessage = fakeAnswerSessionMessage()
        val expectedIceCandidateMessage = fakeIceCandidateMessage()

        val job = launch {
            signalingProtocol.onReceiveOffer().collect { values.add(it) }
        }
        webSocketController.sendMessage(expectedAnswerMessage)
        webSocketController.sendMessage(expectedOfferMessage)
        webSocketController.sendMessage(expectedIceCandidateMessage)

        Assertions.assertThat(values[0]).usingRecursiveComparison().isEqualTo(expectedOfferMessage)
        Assertions.assertThat(values).hasSize(1)
        job.cancel()
    }

    @Test
    fun onReceiveAnswer() = runBlockingTest {
        val values = mutableListOf<AnswerMessage>()
        val expectedOfferMessage = fakeOfferSessionMessage()
        val expectedAnswerMessage = fakeAnswerSessionMessage()
        val expectedIceCandidateMessage = fakeIceCandidateMessage()

        val job = launch {
            signalingProtocol.onReceiveAnswer().collect { values.add(it) }
        }
        webSocketController.sendMessage(expectedAnswerMessage)
        webSocketController.sendMessage(expectedOfferMessage)
        webSocketController.sendMessage(expectedIceCandidateMessage)

        Assertions.assertThat(values[0]).usingRecursiveComparison().isEqualTo(expectedAnswerMessage)
        Assertions.assertThat(values).hasSize(1)
        job.cancel()
    }

    @Test
    fun onReceiveIceCandidate() = runBlockingTest {
        val values = mutableListOf<IceCandidateMessage>()
        val expectedOfferMessage = fakeOfferSessionMessage()
        val expectedAnswerMessage = fakeAnswerSessionMessage()
        val expectedIceCandidateMessage = fakeIceCandidateMessage()

        val job = launch {
            signalingProtocol.onReceiveIceCandidate().collect { values.add(it) }
        }
        webSocketController.sendMessage(expectedAnswerMessage)
        webSocketController.sendMessage(expectedOfferMessage)
        webSocketController.sendMessage(expectedIceCandidateMessage)

        Assertions.assertThat(values[0]).usingRecursiveComparison()
            .isEqualTo(expectedIceCandidateMessage)
        Assertions.assertThat(values).hasSize(1)
        job.cancel()
    }
}