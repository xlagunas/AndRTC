package cat.xlagunas.user.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.user.User
import cat.xlagunas.user.auth.AuthenticationRepository
import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.navigation.Navigator
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class RegisterViewModel @Inject constructor(
    private val userRepository: AuthenticationRepository,
    private val navigator: Navigator
) :
    DisposableViewModel() {

    private val user: MutableLiveData<User> = MutableLiveData()
    val onRegistrationError: MutableLiveData<RegistrationError> = MutableLiveData()

    fun register(user: User) {
        disposable.add(
            userRepository.registerUser(user).subscribe(
                { navigator.navigateToProfile() },
                { error -> onRegistrationError.value = RegistrationError(error.message) })
        )
    }

    fun findUser(): LiveData<User> {
        disposable.add(
            userRepository.findUser()
                .subscribe(user::postValue, Timber::e)
        )
        return user
    }
}
