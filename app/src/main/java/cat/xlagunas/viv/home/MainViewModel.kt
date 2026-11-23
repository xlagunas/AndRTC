package cat.xlagunas.viv.home

import androidx.lifecycle.LiveData
import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.common.toLiveData
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.user.auth.AuthenticationRepository
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class MainViewModel
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val navigator: Navigator,
    ) :
    DisposableViewModel() {
        fun navigateToContacts() {
            navigator.navigateToContacts()
        }

        fun navigateToProfile() {
            // isUserLoggedIn() never completes: take only the current value so later login
            // status changes don't trigger stale navigations
            disposable +=
                authenticationRepository.isUserLoggedIn()
                    .firstOrError()
                    .subscribe(
                        { loggedIn ->
                            when (loggedIn) {
                                true -> navigator.navigateToProfile()
                                false -> navigator.navigateToLogin()
                            }
                        },
                        { error ->
                            Timber.e(error, "Error checking login status")
                            navigator.navigateToLogin()
                        },
                    )
        }

        val isUserLoggedIn: LiveData<Boolean>
            get() =
                authenticationRepository
                    .isUserLoggedIn()
                    .toLiveData()
    }
