package cat.xlagunas.viv.profile

import androidx.lifecycle.SingleLiveEvent
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import cat.xlagunas.viv.commons.extension.toLiveData
import cat.xlagunas.viv.login.InvalidLoginState
import cat.xlagunas.viv.login.LoginState
import cat.xlagunas.viv.login.Logout
import cat.xlagunas.viv.login.SuccessLoginState
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val pushTokenRepository: PushTokenRepository
) : DisposableViewModel() {

    val loginDataEvent = SingleLiveEvent<LoginState>()

    fun getLoginStatus() {
        disposable += authenticationRepository
            .isUserLoggedIn()
            .subscribe(
                { loginDataEvent.value = mapLoginState(it) },
                { error -> loginDataEvent.value = InvalidLoginState(error.localizedMessage) })
    }

    val user = this.authenticationRepository.getUser().toLiveData()

    fun logout() {
        disposable +=
            pushTokenRepository.invalidatePushToken()
                .andThen(authenticationRepository.logoutUser())
                .subscribe { loginDataEvent.value = Logout }
    }

    private fun mapLoginState(loggedIn: Boolean): LoginState {
        return if (loggedIn) {
            SuccessLoginState
        } else {
            InvalidLoginState("Logged out")
        }
    }
}