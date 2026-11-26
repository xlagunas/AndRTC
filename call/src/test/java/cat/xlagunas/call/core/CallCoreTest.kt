package cat.xlagunas.call.core

import androidx.lifecycle.viewModelScope
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import cat.xlagunas.call.CallState
import cat.xlagunas.call.CallViewModel
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.NavigationCall
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.FakeResponse
import cat.xlagunas.test.utils.http.ResponseGate
import cat.xlagunas.test.utils.http.disconnect
import cat.xlagunas.test.utils.http.heldBy
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.malformedJson
import cat.xlagunas.test.utils.http.ok
import cat.xlagunas.test.utils.recordStates
import kotlinx.coroutines.cancel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CallCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    private lateinit var harness: CallCoreHarness

    @Before
    fun setUp() {
        harness = CallCoreHarness(backend, rule)
    }

    @Test
    fun givenBackendCreatesCall_whenCreateCall_thenStartedAndNavigatesToRoom() {
        givenBackendCreatesCall()

        val states = createCall(5)

        assertThat(states.awaitCount(3)).containsExactly(CallState.Idle, CallState.Creating, CallState.Started)
        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.StartCall("room-1"))
    }

    @Test
    fun givenBackendCreatesCall_whenCreateCall_thenSendsParticipants() {
        givenBackendCreatesCall()

        createCall(5, 6).awaitCount(3)

        val request = backend.awaitRequest()
        assertThat(request.method).isEqualTo("PUT")
        assertThat(request.path).isEqualTo("/call/")
        assertThat(request.body).isEqualTo("""{"participants":[{"id":5},{"id":6}]}""")
    }

    @Test
    fun givenUnauthorized_whenCreateCall_thenErrorAndNoNavigation() {
        assertCreateCallFailsWith(httpError(401))
    }

    @Test
    fun givenForbidden_whenCreateCall_thenErrorAndNoNavigation() {
        assertCreateCallFailsWith(httpError(403))
    }

    @Test
    fun givenNotFound_whenCreateCall_thenErrorAndNoNavigation() {
        assertCreateCallFailsWith(httpError(404))
    }

    @Test
    fun givenServerError_whenCreateCall_thenErrorAndNoNavigation() {
        assertCreateCallFailsWith(httpError(500))
    }

    @Test
    fun givenDisconnect_whenCreateCall_thenErrorAndNoNavigation() {
        assertCreateCallFailsWith(disconnect())
    }

    @Test
    fun givenMalformedResponse_whenCreateCall_thenErrorAndNoNavigation() {
        assertCreateCallFailsWith(malformedJson())
    }

    @Test
    fun givenCallBeingCreated_whenCreateCallAgain_thenSingleRequestAndSingleCreatingState() {
        val gate = ResponseGate()
        backend.respondTo("PUT", "/call/", ok("call/call_created.json").heldBy(gate))
        val viewModel = harness.callViewModel()
        val states = recordStates(viewModel.state)

        viewModel.createCall(listOf(5))
        viewModel.createCall(listOf(5))
        backend.awaitRequest()
        gate.release()

        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.StartCall("room-1"))
        assertThat(states.awaitCount(3)).containsExactly(CallState.Idle, CallState.Creating, CallState.Started)
        assertThat(backend.requests).hasSize(1)
    }

    @Test
    fun givenViewModelCleared_whenBackendAnswers_thenStillStartsCall() {
        val gate = ResponseGate()
        backend.respondTo("PUT", "/call/", ok("call/call_created.json").heldBy(gate))
        val viewModel = harness.callViewModel()

        viewModel.createCall(listOf(5))
        backend.awaitRequest()
        // What ViewModel.onCleared does to viewModelScope
        viewModel.viewModelScope.cancel()
        gate.release()

        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.StartCall("room-1"))
    }

    private fun assertCreateCallFailsWith(response: FakeResponse) {
        backend.respondTo("PUT", "/call/", response)

        val states = createCall(5)

        assertThat(states.awaitCount(3)).containsExactly(CallState.Idle, CallState.Creating, CallState.Error)
        assertThat(harness.navigator.calls.values).isEmpty()
    }

    private fun givenBackendCreatesCall() {
        backend.respondTo("PUT", "/call/", ok("call/call_created.json"))
    }

    private fun createCall(vararg ids: Long) =
        harness.callViewModel().let { viewModel: CallViewModel ->
            recordStates(viewModel.state).also { viewModel.createCall(ids.toList()) }
        }
}
