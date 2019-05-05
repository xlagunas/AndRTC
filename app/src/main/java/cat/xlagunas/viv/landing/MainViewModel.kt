package cat.xlagunas.viv.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.SingleLiveEvent
import cat.xlagunas.user.domain.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import cat.xlagunas.viv.commons.extension.toLiveData
import javax.inject.Inject

class MainViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository) :
    DisposableViewModel() {

    private val userLoggedInEvent = SingleLiveEvent<Boolean>()

    val isUserLoggedIn: LiveData<Boolean>
        get() = authenticationRepository
            .isUserLoggedIn()
            .toLiveData()

    init {
        disposable.add(
            authenticationRepository
                .isUserLoggedIn()
                .filter { !it }
                .subscribe { loggedIn -> userLoggedInEvent.value = loggedIn }
        )
    }
}