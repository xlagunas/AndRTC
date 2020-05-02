package cat.xlagunas.user.profile

import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.common.toLiveData
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.user.auth.AuthenticationRepository
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val pushTokenRepository: PushTokenRepository,
    private val navigator: Navigator
) : DisposableViewModel() {
    val user = this.authenticationRepository.getUser().toLiveData()

    fun logout() {
        pushTokenRepository.invalidatePushToken()
            .andThen(authenticationRepository.logoutUser())
            .subscribe {
                navigator.navigateToLogin()
            }.apply { disposable.addAll(this) }
    }
}