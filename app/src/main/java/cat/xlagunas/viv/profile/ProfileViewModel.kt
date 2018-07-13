package cat.xlagunas.viv.profile

import android.arch.lifecycle.MutableLiveData
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : DisposableViewModel() {

    val user: MutableLiveData<User> by lazy {
        disposable.add(
            authenticationRepository.findUser()
                .subscribe(
                    { user.value = it!! }, Timber::e, { user.value = null })
        )
        MutableLiveData<User>()
    }
}