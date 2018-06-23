package cat.xlagunas.viv.push

import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import timber.log.Timber
import javax.inject.Inject

class PushTokenViewModel @Inject
constructor(private val authenticationRepository: AuthenticationRepository) : DisposableViewModel() {

    fun registerToken() {
        disposable.add(authenticationRepository.registerPushToken()
                .subscribe({ Timber.d("Push token successfully registered") }, { Timber.e(it, "Error requesting token") })
        )
    }

    fun isPushTokenRegistered() = authenticationRepository.isPushTokenRegistered()
}