package cat.xlagunas.data.call

import cat.xlagunas.core.data.converter.FriendConverter
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.call.Call
import cat.xlagunas.domain.call.CallRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.Instant

class CallRepositoryImplTest {

    @Mock
    lateinit var callApi: CallApi

    private lateinit var callRepository: CallRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val schedulers =
            RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline())

        callRepository = CallRepositoryImpl(callApi, FriendConverter(), CallConverter(),schedulers)
    }

    @Test
    fun whenCreateCall_thenCreateCallRequest() {
        val fakeCallDto = CallDto("theRoomId", Instant.now())
        val fakeCall = Call(fakeCallDto.roomId, fakeCallDto.date)
        val callParticipants = CallParticipantsDto(listOf(CallParticipantDto(5)))

        `when`(callApi.createCall(callParticipants)).thenReturn(Single.just(fakeCallDto))

        callRepository.createCall(listOf(generateFakeFriend(5)))
            .test()
            .assertValue(fakeCall)
            .assertComplete()
    }

    @Test
    fun whenAcceptCall_thenAcceptCallRequest() {
        throw IllegalStateException()
    }

    @Test
    fun whenRejectCall_thenRejectCallRequest() {
        throw IllegalStateException()
    }

    private fun generateFakeFriend(friendId: Long): Friend {
        return Friend(friendId, "aUsername", "aName", null, "anEmail@email.com", "ACCEPTED")
    }
}