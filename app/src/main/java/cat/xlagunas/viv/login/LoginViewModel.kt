package cat.xlagunas.viv.login

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import cat.xlagunas.data.user.login.GoogleSignInDataSource
import cat.xlagunas.domain.user.register.RegisterRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.Lazy
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject


class LoginViewModel @Inject constructor(private val googleSignInDataSource: Lazy<GoogleSignInDataSource>, private val registerRepository: RegisterRepository) : ViewModel() {

    private val liveData: MutableLiveData<LoginState> = MutableLiveData()
    private var disposable: Disposable? = null


    fun registerGoogle(): LifecycleObserver = googleSignInDataSource.get()

    fun initGoogleSignIn() {
        googleSignInDataSource.get().signIn()
    }

    fun handleLoginResult(task: Task<GoogleSignInAccount>) {
        disposable = googleSignInDataSource.get().handleSigninResult(task)
                .flatMapCompletable { registerRepository.registerUser(it) }
                .subscribe({ emitNextLoginState(true) }, { emitNextLoginState(false) })
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
        disposable?.dispose()
    }

}


data class LoginState(val isSuccess: Boolean)

