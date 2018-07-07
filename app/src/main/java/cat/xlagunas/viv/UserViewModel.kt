package cat.xlagunas.viv

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.SingleLiveEvent
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import javax.inject.Inject

class UserViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository) : DisposableViewModel() {

    private val userLoggedInEvent = SingleLiveEvent<Boolean>()

    val isUserLoggedIn: LiveData<Boolean>
        get() = LiveDataReactiveStreams.fromPublisher(authenticationRepository
                .isUserLoggedIn()
                .filter { !it })


    fun getCurrentUser(): LiveData<User> {
        return LiveDataReactiveStreams.fromPublisher(authenticationRepository.getUser())
    }

    init {
        disposable.add(
                authenticationRepository
                        .isUserLoggedIn()
                        .filter { !it }
                        .subscribe { loggedIn -> userLoggedInEvent.value = loggedIn }
        )
    }
}