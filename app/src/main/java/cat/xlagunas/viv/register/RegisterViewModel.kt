package cat.xlagunas.viv.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.user.domain.AuthenticationRepository
import cat.xlagunas.core.common.DisposableViewModel
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class RegisterViewModel @Inject constructor(private val userRepository: AuthenticationRepository) :
    DisposableViewModel() {

    private val user: MutableLiveData<User> = MutableLiveData()
    val registrationState: MutableLiveData<RegistrationState> = MutableLiveData()

    fun register(user: User) {
        disposable.add(
            userRepository.registerUser(user).subscribe(
                { registrationState.value = Success },
                { error -> registrationState.value = RegistrationError(error.message) })
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
