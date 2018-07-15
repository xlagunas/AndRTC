package cat.xlagunas.viv.login

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.data.user.login.GoogleSignInDataSource
import cat.xlagunas.domain.user.authentication.AuthenticationCredentials
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.Lazy
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class LoginViewModel @Inject constructor(
    private val googleSignInDataSource: Lazy<GoogleSignInDataSource>,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val liveData: MutableLiveData<LoginState> = MutableLiveData()
    private val disposable = CompositeDisposable()

    fun registerGoogle(): LifecycleObserver = googleSignInDataSource.get()

    fun initGoogleSignIn() {
        googleSignInDataSource.get().signIn()
    }

    fun handleLoginResult(task: Task<GoogleSignInAccount>) {
        disposable.add(googleSignInDataSource.get().handleSignInResult(task)
            .flatMapCompletable { authenticationRepository.registerUser(it) }
            .doOnComplete { Timber.d("Successfully registered in database") }
            .subscribe(this::onSuccessfullyLogged, this::handleErrorState))
    }

    fun login(username: String, password: String) {
        disposable.add(
            authenticationRepository.login(AuthenticationCredentials(username, password))
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

