package cat.xlagunas.conference.data

import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.Conferencee
import cat.xlagunas.conference.domain.WsMessagingApi
import com.tinder.scarlet.WebSocket
import io.reactivex.Flowable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ConferenceRepositoryImp @Inject constructor(
    private val messagingApi: WsMessagingApi
) : ConferenceRepository {

    override fun joinRoom() {

        GlobalScope.launch {
            messagingApi.observeMessageEvent()
                .filter { it is WebSocket.Event.OnConnectionOpened<*> }
                .consumeEach { messagingApi.sendMessage(MessageDto("testUser", "Hello from Android", MessageType.SERVER, "JOIN")) }

        }

        GlobalScope.launch {
            val users = messagingApi.getRoomParticipants().receive()
            for (user in users) {
                Timber.d("Room participant: ${user.userId}")
            }

            messagingApi.observeSessionStream()
                .consumeEach { message -> Timber.d("New message received: $message.data") }

            messagingApi.observeIceCandidateStream()
                .consumeEach { message -> Timber.d("New message received: $message.data") }
        }
    }

    override fun getConnectedUsers(): Flowable<Conferencee> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun logoutRoom() {
        Timber.d("Logging out room!")
    }
}