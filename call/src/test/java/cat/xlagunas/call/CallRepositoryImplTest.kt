package cat.xlagunas.call

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
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
        callRepository = CallRepositoryImpl(callApi, CallConverter(Gson()))
    }

    @Test
    fun whenCreateCall_thenCreateCallRequest() = runBlocking {
        val fakeCallDto = CallDto("theRoomId")
        val fakeCall = Call(fakeCallDto.roomId)
        val callParticipants = CallParticipantsDto(listOf(CallParticipantDto(5)))
        `when`(callApi.createCall(callParticipants)).thenReturn(fakeCallDto)

        val call = callRepository.createCall(listOf(5))
        assertThat(call).isEqualToComparingFieldByField(fakeCall)
        return@runBlocking
    }
}