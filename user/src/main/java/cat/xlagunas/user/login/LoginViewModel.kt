package cat.xlagunas.user.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.user.auth.AuthenticationCredentials
import cat.xlagunas.user.auth.AuthenticationRepository
import javax.inject.Inject
import retrofit2.HttpException
import timber.log.Timber

@OpenForTesting
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val pushTokenRepository: PushTokenRepository,
    private val navigator: Navigator
) : DisposableViewModel() {

    private val liveData: LiveData<LoginState> = MutableLiveData()

    fun login(username: String, password: String) {
        disposable.add(
            authenticationRepository.login(
                AuthenticationCredentials(
                    username,
                    password
                )
            )
                .andThen(pushTokenRepository.registerPushToken())
                .subscribe(this::onSuccessfullyLogged, this::handleErrorState)
        )
    }

    private fun onSuccessfullyLogged() {
        (liveData as MutableLiveData).postValue(SuccessLoginState)
        navigator.navigateToProfile()
    }

    private fun handleErrorState(throwable: Throwable) {
        Timber.e(throwable, "Error logging in user")
        val stateError = if (throwable is HttpException) {
            when (throwable.code()) {
                409 -> InvalidLoginState("User already registered")
                401 -> InvalidLoginState("Authentication error")
                else -> InvalidLoginState(throwable.message())
            }
        } else InvalidLoginState(throwable.message ?: "Something went wrong")

        (liveData as MutableLiveData).postValue(stateError)
    }

    fun onLoginStateChange(): LiveData<LoginState> {
        return liveData
    }

    fun navigateToRegistration() {
        navigator.navigateToRegistration()
    }
}
