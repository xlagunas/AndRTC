package cat.xlagunas.user.register

import androidx.lifecycle.MutableLiveData
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.user.User
import cat.xlagunas.user.auth.AuthenticationRepository
import javax.inject.Inject

@OpenForTesting
class RegisterViewModel @Inject constructor(
    private val userRepository: AuthenticationRepository,
    private val navigator: Navigator
) :
    DisposableViewModel() {

    val onRegistration: MutableLiveData<RegistrationState> = MutableLiveData()

    fun register(user: User) {
        disposable.add(
            userRepository.registerUser(user).subscribe(
                {
                    onRegistration.value = Success
                    navigator.navigateToLogin()
                },
                { error -> onRegistration.value = RegistrationError(error.message) })
        )
    }
}
