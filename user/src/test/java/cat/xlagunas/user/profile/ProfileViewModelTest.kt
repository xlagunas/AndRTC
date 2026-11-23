package cat.xlagunas.user.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.user.User
import cat.xlagunas.user.auth.AuthenticationRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ProfileViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val authenticationRepository = mock(AuthenticationRepository::class.java)
    private val pushTokenRepository = mock(PushTokenRepository::class.java)
    private val navigator = mock(Navigator::class.java)

    private val user = User(1L, "aUser", "First", "Last", "user@example.com", "", null)

    @Test
    fun givenUserUpdates_whenObservingUser_thenLatestUserEmitted() {
        val userUpdates = PublishProcessor.create<User>()
        `when`(authenticationRepository.getUser()).thenReturn(userUpdates)
        val viewModel = ProfileViewModel(authenticationRepository, pushTokenRepository, navigator)
        val emitted = mutableListOf<User>()

        viewModel.user.observeForever { emitted.add(it) }
        userUpdates.onNext(user)
        userUpdates.onNext(user.copy(firstName = "Updated"))

        assertThat(emitted).containsExactly(user, user.copy(firstName = "Updated"))
    }

    @Test
    fun whenLogout_thenInvalidatePushTokenLogoutAndNavigateToLogin() {
        `when`(authenticationRepository.getUser()).thenReturn(Flowable.never())
        val executed = mutableListOf<String>()
        `when`(pushTokenRepository.invalidatePushToken())
            .thenReturn(Completable.fromAction { executed.add("invalidatePushToken") })
        `when`(authenticationRepository.logoutUser())
            .thenReturn(Completable.fromAction { executed.add("logoutUser") })
        `when`(navigator.navigateToLogin()).then { executed.add("navigateToLogin") }
        val viewModel = ProfileViewModel(authenticationRepository, pushTokenRepository, navigator)

        viewModel.logout()

        assertThat(executed).containsExactly("invalidatePushToken", "logoutUser", "navigateToLogin")
    }
}
