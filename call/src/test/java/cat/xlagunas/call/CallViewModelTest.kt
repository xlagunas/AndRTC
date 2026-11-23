package cat.xlagunas.call

import cat.xlagunas.core.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class CallViewModelTest {
    private val callRepository = mock(CallRepository::class.java)
    private val navigator = mock(Navigator::class.java)
    private val viewModel = CallViewModel(callRepository, navigator)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenCreateCall_thenStartCallWithCreatedRoom() {
        runBlocking { `when`(callRepository.createCall(listOf(5L, 6L))).thenReturn(Call("theRoomId")) }

        viewModel.createCall(listOf(5L, 6L))

        // CallViewModel hardcodes Dispatchers.IO, so the call completes on a background thread
        verify(navigator, timeout(TIMEOUT_MS)).startCall("theRoomId")
    }

    private companion object {
        const val TIMEOUT_MS = 1_000L
    }
}
