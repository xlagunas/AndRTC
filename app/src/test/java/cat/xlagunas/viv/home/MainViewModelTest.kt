package cat.xlagunas.viv.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.user.auth.AuthenticationRepository
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val authenticationRepository = mock(AuthenticationRepository::class.java)
    private val navigator = mock(Navigator::class.java)

    @Test
    fun whenNavigateToContacts_thenNavigatorCalled() {
        val viewModel = givenViewModel(loggedIn = true)

        viewModel.navigateToContacts()

        verify(navigator).navigateToContacts()
    }

    @Test
    fun givenUserLoggedIn_whenNavigateToProfile_thenProfileShown() {
        val viewModel = givenViewModel(loggedIn = true)

        viewModel.navigateToProfile()

        verify(navigator).navigateToProfile()
        verify(navigator, never()).navigateToLogin()
    }

    @Test
    fun givenUserNotLoggedIn_whenNavigateToProfile_thenLoginShown() {
        val viewModel = givenViewModel(loggedIn = false)

        viewModel.navigateToProfile()

        verify(navigator).navigateToLogin()
        verify(navigator, never()).navigateToProfile()
    }

    @Test
    fun givenLoginStatusChangesLater_whenNavigateToProfile_thenOnlyCurrentStatusUsed() {
        val loggedIn = BehaviorProcessor.createDefault(false)
        `when`(authenticationRepository.isUserLoggedIn()).thenReturn(loggedIn)
        val viewModel = MainViewModel(authenticationRepository, navigator)

        viewModel.navigateToProfile()
        loggedIn.onNext(true)

        verify(navigator, times(1)).navigateToLogin()
        verify(navigator, never()).navigateToProfile()
        assertThat(loggedIn.hasSubscribers()).isFalse()
    }

    @Test
    fun givenLoginStatusError_whenNavigateToProfile_thenLoginShown() {
        `when`(authenticationRepository.isUserLoggedIn()).thenReturn(Flowable.error(IllegalStateException()))
        val viewModel = MainViewModel(authenticationRepository, navigator)

        viewModel.navigateToProfile()

        verify(navigator).navigateToLogin()
    }

    @Test
    fun givenLoginStatusChanges_whenObservingIsUserLoggedIn_thenEachStatusEmitted() {
        val loggedIn = BehaviorProcessor.createDefault(false)
        `when`(authenticationRepository.isUserLoggedIn()).thenReturn(loggedIn)
        val viewModel = MainViewModel(authenticationRepository, navigator)
        val emitted = mutableListOf<Boolean>()

        viewModel.isUserLoggedIn.observeForever { emitted.add(it) }
        loggedIn.onNext(true)

        assertThat(emitted).containsExactly(false, true)
    }

    private fun givenViewModel(loggedIn: Boolean): MainViewModel {
        `when`(authenticationRepository.isUserLoggedIn()).thenReturn(Flowable.just(loggedIn))
        return MainViewModel(authenticationRepository, navigator)
    }
}
