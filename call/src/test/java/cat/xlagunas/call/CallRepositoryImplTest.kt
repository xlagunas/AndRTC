package cat.xlagunas.call

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class CallRepositoryImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var callApi: CallApi
    private lateinit var callRepository: CallRepository

    @Before
    fun setUp() {
        callRepository = CallRepositoryImpl(callApi, CallConverter(Gson()))
    }

    @Test
    fun whenCreateCall_thenCreateCallRequest() =
        runBlocking {
            val fakeCallDto = CallDto("theRoomId")
            val fakeCall = Call(fakeCallDto.roomId)
            val callParticipants = CallParticipantsDto(listOf(CallParticipantDto(5)))
            `when`(callApi.createCall(callParticipants)).thenReturn(fakeCallDto)

            val call = callRepository.createCall(listOf(5))
            assertThat(call).usingRecursiveComparison().isEqualTo(fakeCall)
            return@runBlocking
        }
}
