package cat.xlagunas.user.profile

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

        fun logout() {
            disposable.add(
                pushTokenRepository.invalidatePushToken()
                    // Failing to invalidate the push token (e.g. offline) must not block a local logout
                    .doOnError { Timber.w(it, "Error invalidating push token") }
                    .onErrorComplete()
                    .andThen(Completable.defer { authenticationRepository.logoutUser() })
                    .subscribe(
                        { navigator.navigateToLogin() },
                        { Timber.e(it, "Error logging out user") },
                    ),
            )
        }
    }
