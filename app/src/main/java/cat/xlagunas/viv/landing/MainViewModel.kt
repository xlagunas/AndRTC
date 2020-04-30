package cat.xlagunas.viv.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.SingleLiveEvent
import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.common.toLiveData
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.user.domain.AuthenticationRepository
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val navigator: Navigator
) :
    DisposableViewModel() {

    fun navigateToContacts() {
        navigator.navigateToContacts()
    }

    fun navigateToProfile() {
        disposable += authenticationRepository.isUserLoggedIn()
            .subscribe {
                when (it) {
                    true -> navigator.navigateToProfile()
                    false -> navigator.navigateToLogin()
                }
            }
    }

    private val userLoggedInEvent = SingleLiveEvent<Boolean>()

    val isUserLoggedIn: LiveData<Boolean>
        get() = authenticationRepository
            .isUserLoggedIn()
            .toLiveData()

    init {
        disposable +=
            authenticationRepository
                .isUserLoggedIn()
                .filter { !it }
                .subscribe { loggedIn -> userLoggedInEvent.value = loggedIn }
    }
}