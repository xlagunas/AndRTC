package cat.xlagunas.conference.data

import cat.xlagunas.conference.domain.IceCandidateMessage
import com.google.gson.Gson
import com.tinder.scarlet.MessageAdapter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.webrtc.IceCandidate

class WebRtcMessageAdapterTest {

    private lateinit var messageFactory: MessageAdapter<Any>
    private val gson = Gson()

    @Before
    fun setup() {
        messageFactory = WebRtcMessageAdapter.Factory("fakeUser").create(Any::class.java, emptyArray())
    }

    @Test
    fun whenMessageReceived_thenSuccessfullyParsed() {
        val iceCandidateContent = IceCandidateMessage(IceCandidate("an", 1, "sdpMessage"), "fakeUser")
        val messageDto = MessageDto("", gson.toJson(iceCandidateContent), MessageType.ICE_CANDIDATE, "fakeUser")

        val message = messageFactory.toMessage(messageDto)
        val unwrapped: IceCandidateMessage = messageFactory.fromMessage(message) as IceCandidateMessage

        assertThat(unwrapped).isEqualToComparingFieldByFieldRecursively(iceCandidateContent)
    }

    @Test
    fun whenMessageSent_thenSuccessfullyConverted() {
        val iceCandidateContent = IceCandidateMessage(IceCandidate("an", 1, "sdpMessage"), "fakeUser")
        val messageDto = MessageDto("", gson.toJson(iceCandidateContent), MessageType.ICE_CANDIDATE, "fakeUser")

        val message = messageFactory.toMessage(messageDto)
        val iceCandidateMessage = messageFactory.fromMessage(message)

        assertThat(iceCandidateMessage).isEqualToComparingFieldByFieldRecursively(iceCandidateContent)
    }
}