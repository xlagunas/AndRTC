package cat.xlagunas.viv.login

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import cat.xlagunas.data.user.login.GoogleSignInDataSource
import cat.xlagunas.domain.user.authentication.AuthenticationCredentials
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.Lazy
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class LoginViewModel @Inject constructor(
        private val googleSignInDataSource: Lazy<GoogleSignInDataSource>,
        private val authenticationRepository: AuthenticationRepository) : ViewModel() {

    private val liveData: MutableLiveData<LoginState> = MutableLiveData()
    private val disposable = CompositeDisposable()


    fun registerGoogle(): LifecycleObserver = googleSignInDataSource.get()

    fun initGoogleSignIn() {
        googleSignInDataSource.get().signIn()
    }

    fun handleLoginResult(task: Task<GoogleSignInAccount>) {
        disposable.add(googleSignInDataSource.get().handleSignInResult(task)
                .flatMapCompletable { authenticationRepository.registerUser(it) }
                .subscribe({ emitNextLoginState(true) }, { emitNextLoginState(false) }))
    }

    fun login(username: String, password: String) {
        disposable.add(
                authenticationRepository.login(AuthenticationCredentials(username, password))
                        .subscribe({ emitNextLoginState(true) }, { emitNextLoginState(false) }))
    }

    private fun handleErrorState(throwable: Throwable) {
        Timber.e(throwable, "Error registering user")
        if (throwable is HttpException) {
            when (throwable.code()) {
                409 -> LoginState(false)
            }
        }

    }

    fun onLoginStateChange(): LiveData<LoginState> {
        return liveData
    }

    private fun emitNextLoginState(state: Boolean) {
        liveData.postValue(LoginState(state))
    }


    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}


data class LoginState(val isSuccess: Boolean)

