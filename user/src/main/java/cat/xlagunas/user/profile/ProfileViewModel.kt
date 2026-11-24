package cat.xlagunas.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.common.toLiveData
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.user.auth.AuthenticationRepository
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val pushTokenRepository: PushTokenRepository,
        private val navigator: Navigator,
    ) : DisposableViewModel() {
        val user = this.authenticationRepository.getUser().toLiveData()

        private val _logoutState = MutableLiveData<LogoutState>(LogoutState.Idle)
        val logoutState: LiveData<LogoutState> = _logoutState

        fun logout() {
            if (_logoutState.value == LogoutState.InProgress) return
            _logoutState.value = LogoutState.InProgress
            disposable.add(
                pushTokenRepository.invalidatePushToken()
                    .andThen(Completable.defer { authenticationRepository.logoutUser() })
                    .subscribe(
                        { navigator.navigateToLogin() },
                        {
                            Timber.e(it, "Error logging out user")
                            _logoutState.postValue(LogoutState.Error)
                        },
                    ),
            )
        }

        fun onLogoutErrorShown() {
            if (_logoutState.value == LogoutState.Error) _logoutState.value = LogoutState.Idle
        }
    }
