package cat.xlagunas.call

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import cat.xlagunas.core.navigation.Navigator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CallViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val navigator = mock(Navigator::class.java)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenCreateCall_thenStartCallWithCreatedRoom() =
        runTest(testDispatcher) {
            val viewModel = givenViewModel(FakeCallRepository { Call("theRoomId") })

            val states = observeStates(viewModel)

            viewModel.createCall(listOf(5L, 6L))

            verify(navigator).startCall("theRoomId")
            assertThat(states).containsExactly(CallState.Idle, CallState.Creating, CallState.Started)
        }

    @Test
    fun givenCreateCallFails_whenCreateCall_thenErrorAndNoNavigation() =
        runTest(testDispatcher) {
            val viewModel = givenViewModel(FakeCallRepository { throw IOException("No connection") })
            val states = observeStates(viewModel)

            viewModel.createCall(listOf(5L))

            verifyNoInteractions(navigator)
            assertThat(states).containsExactly(CallState.Idle, CallState.Creating, CallState.Error)
        }

    @Test
    fun givenStartCallFails_whenCreateCall_thenStartedWithoutRetry() =
        runTest(testDispatcher) {
            val repository = FakeCallRepository { Call("theRoomId") }
            doThrow(IllegalStateException("No activity")).`when`(navigator).startCall("theRoomId")
            val viewModel = givenViewModel(repository)
            val states = observeStates(viewModel)

            viewModel.createCall(listOf(5L))

            // The call already exists on the backend, so offering Retry would create a second one
            assertThat(states).containsExactly(CallState.Idle, CallState.Creating, CallState.Started)
            assertThat(repository.requestedContacts).containsExactly(listOf(5L))
        }

    @Test
    fun givenCreateCallFailed_whenRetrySucceeds_thenStartCall() =
        runTest(testDispatcher) {
            var calls = 0
            val viewModel =
                givenViewModel(
                    FakeCallRepository {
                        if (calls++ == 0) throw IOException("No connection") else Call("theRoomId")
                    },
                )
            val states = observeStates(viewModel)

            viewModel.createCall(listOf(5L))
            viewModel.createCall(listOf(5L))

            verify(navigator).startCall("theRoomId")
            assertThat(states.last()).isEqualTo(CallState.Started)
        }

    @Test
    fun givenCallBeingCreated_whenCreateCallAgain_thenSingleRequest() =
        runTest(testDispatcher) {
            val pendingCall = CompletableDeferred<Call>()
            val repository = FakeCallRepository { pendingCall.await() }
            val viewModel = givenViewModel(repository)

            viewModel.createCall(listOf(5L))
            viewModel.createCall(listOf(5L))

            assertThat(repository.requestedContacts).hasSize(1)
        }

    @Test
    fun givenViewModelClearedWhileCreatingCall_whenCallCreated_thenStillStartCall() =
        runTest(testDispatcher) {
            val pendingCall = CompletableDeferred<Call>()
            val viewModel = givenViewModel(FakeCallRepository { pendingCall.await() })

            viewModel.createCall(listOf(5L))
            // Simulates the confirmation dialog being dismissed right after confirming
            viewModel.viewModelScope.cancel()
            pendingCall.complete(Call("theRoomId"))

            verify(navigator).startCall("theRoomId")
        }

    @Test
    fun whenCreateCall_thenRequestedContactsPassedToRepository() =
        runTest(testDispatcher) {
            val repository = FakeCallRepository { Call("theRoomId") }
            val viewModel = givenViewModel(repository)

            viewModel.createCall(listOf(5L, 6L))

            assertThat(repository.requestedContacts).containsExactly(listOf(5L, 6L))
        }

    private fun observeStates(viewModel: CallViewModel): List<CallState> {
        val states = mutableListOf<CallState>()
        viewModel.state.observeForever { states.add(it) }
        return states
    }

    private fun givenViewModel(repository: CallRepository) = CallViewModel(repository, navigator, testDispatcher)

    private class FakeCallRepository(private val onCreateCall: suspend () -> Call) : CallRepository {
        val requestedContacts = mutableListOf<List<Long>>()

        override suspend fun createCall(contacts: List<Long>): Call {
            requestedContacts.add(contacts)
            return onCreateCall()
        }

        override suspend fun acceptCall(callId: Long) = Unit

        override suspend fun rejectCall(callId: Long) = Unit
    }
}
