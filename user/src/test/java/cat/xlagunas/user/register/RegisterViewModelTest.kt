package cat.xlagunas.user.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.user.User
import cat.xlagunas.user.auth.AuthenticationRepository
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.io.IOException

class RegisterViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val authenticationRepository = mock(AuthenticationRepository::class.java)
    private val navigator = mock(Navigator::class.java)

    private lateinit var viewModel: RegisterViewModel
    private val states = mutableListOf<RegistrationState>()

    private val user = User("acalpe", "Anna Maria", "Calpe Valls", "anna@example.com", "", "123456")

    @Before
    fun setUp() {
        viewModel = RegisterViewModel(authenticationRepository, navigator)
        viewModel.onRegistration.observeForever { states.add(it) }
    }

    @Test
    fun givenRegistrationSucceeds_whenRegister_thenSuccessAndNavigateToLogin() {
        `when`(authenticationRepository.registerUser(user)).thenReturn(Completable.complete())

        viewModel.register(user)

        assertThat(states).containsExactly(Success)
        verify(navigator).navigateToLogin()
    }

    @Test
    fun givenRegistrationFails_whenRegister_thenErrorAndNoNavigation() {
        `when`(authenticationRepository.registerUser(user))
            .thenReturn(Completable.error(IOException("Invalid account data")))

        viewModel.register(user)

        assertThat(states).containsExactly(RegistrationError("Invalid account data"))
        verifyNoInteractions(navigator)
    }
}
