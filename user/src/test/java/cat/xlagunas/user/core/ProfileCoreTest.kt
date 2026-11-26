package cat.xlagunas.user.core

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.NavigationCall
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.recordStates
import cat.xlagunas.user.User
import cat.xlagunas.user.profile.LogoutState
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class ProfileCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    private lateinit var harness: UserCoreHarness

    @Before
    fun setUp() {
        harness = UserCoreHarness(backend, rule)
        harness.givenLoggedInUser()
    }

    @After
    fun tearDown() {
        harness.close()
    }

    @Test
    fun givenLoggedInUser_whenProfileOpened_thenShowsUser() {
        val users = recordStates<User>(harness.profileViewModel().user)

        assertThat(users.awaitValue { it.username == "anna" }.email).isEqualTo("anna@example.com")
    }

    @Test
    fun givenLoggedInUser_whenLogout_thenSessionClearedAndNavigatesToLogin() {
        val viewModel = harness.profileViewModel()
        val states = recordStates<LogoutState>(viewModel.logoutState)

        viewModel.logout()

        assertThat(harness.navigator.calls.awaitCount(1)).containsExactly(NavigationCall.ToLogin)
        assertThat(states.values).containsExactly(LogoutState.Idle, LogoutState.InProgress)
        assertThat(harness.dataStore.isAuthTokenAvailable()).isFalse()
        assertThat(harness.dataStore.getCurrentUserId()).isEqualTo(-1L)
        assertThat(harness.database.userDao().loadAllByUserId(3)).isEmpty()
        assertThat(harness.pushTokenProvider.invalidations).isEqualTo(1)
        assertThat(backend.requests).isEmpty()
    }

    @Test
    fun givenTokenInvalidationFails_whenLogout_thenErrorAndSessionKept() {
        harness.pushTokenProvider.failure = IOException("Firebase unavailable")
        val viewModel = harness.profileViewModel()
        val states = recordStates<LogoutState>(viewModel.logoutState)

        viewModel.logout()

        assertThat(states.awaitCount(3)).containsExactly(LogoutState.Idle, LogoutState.InProgress, LogoutState.Error)
        assertThat(harness.navigator.calls.values).isEmpty()
        assertThat(harness.dataStore.isAuthTokenAvailable()).isTrue()
        assertThat(harness.database.userDao().loadAllByUserId(3).map { it.username }).containsExactly("anna")
    }

    @Test
    fun givenLogoutError_whenErrorShown_thenIdle() {
        harness.pushTokenProvider.failure = IOException("Firebase unavailable")
        val viewModel = harness.profileViewModel()
        val states = recordStates<LogoutState>(viewModel.logoutState)
        viewModel.logout()
        states.awaitCount(3)

        viewModel.onLogoutErrorShown()

        assertThat(states.values.last()).isEqualTo(LogoutState.Idle)
    }
}
