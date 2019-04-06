package cat.xlagunas.data.call

import cat.xlagunas.core.data.converter.FriendConverter
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.call.Call
import cat.xlagunas.domain.call.CallRepository
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CallRepositoryImplTest {

    @Mock
    lateinit var callApi: CallApi

    private lateinit var callRepository: CallRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val schedulers =
            RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline())

        callRepository =
            CallRepositoryImpl(callApi, FriendConverter(), CallConverter(Gson()), schedulers)
    }

    @Test
    fun whenCreateCall_thenCreateCallRequest() {
        val fakeCallDto = CallDto("theRoomId")
        val fakeCall = Call(fakeCallDto.roomId)
        val callParticipants = CallParticipantsDto(listOf(CallParticipantDto(5)))

        `when`(callApi.createCall(callParticipants)).thenReturn(Single.just(fakeCallDto))

        callRepository.createCall(listOf(generateFakeFriend(5)))
            .test()
            .assertValue(fakeCall)
            .assertComplete()
    }

    private fun generateFakeFriend(friendId: Long): Friend {
        return Friend(friendId, "aUsername", "aName", null, "anEmail@email.com", "ACCEPTED")
    }
}