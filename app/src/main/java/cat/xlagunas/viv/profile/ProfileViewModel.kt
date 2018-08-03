package cat.xlagunas.viv.profile

import android.arch.lifecycle.SingleLiveEvent
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import cat.xlagunas.viv.commons.extension.toLiveData
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : DisposableViewModel() {

    val loginDataEvent = SingleLiveEvent<Boolean>()

    fun getLoginStatus() {
        disposable += authenticationRepository
            .isUserLoggedIn()
            .subscribe({ loginDataEvent.value = it }, Timber::e)
    }

    val user = this.authenticationRepository.getUser().toLiveData()

    fun logout() {
        disposable += authenticationRepository.logoutUser().subscribe()
    }
}