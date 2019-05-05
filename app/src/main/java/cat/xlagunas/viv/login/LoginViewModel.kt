package cat.xlagunas.viv.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.user.domain.AuthenticationCredentials
import cat.xlagunas.user.domain.AuthenticationRepository
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val pushTokenRepository: PushTokenRepository
) : ViewModel() {

    private val liveData: MutableLiveData<LoginState> = MutableLiveData()
    private val disposable = CompositeDisposable()

    fun login(username: String, password: String) {
        disposable.add(
            authenticationRepository.login(AuthenticationCredentials(username, password))
                .andThen(pushTokenRepository.registerPushToken())
                .subscribe(this::onSuccessfullyLogged, this::handleErrorState)
        )
    }

    private fun onSuccessfullyLogged() {
        liveData.postValue(SuccessLoginState)
    }

    private fun handleErrorState(throwable: Throwable) {
        Timber.e(throwable, "Error registering user")
        if (throwable is HttpException) {
            when (throwable.code()) {
                409 -> liveData.postValue(InvalidLoginState("User already registered"))
                401 -> liveData.postValue(InvalidLoginState("Authentication error"))
            }
        } else liveData.postValue(InvalidLoginState(throwable.message ?: "Something went wrong"))
    }

    fun onLoginStateChange(): LiveData<LoginState> {
        return liveData
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
