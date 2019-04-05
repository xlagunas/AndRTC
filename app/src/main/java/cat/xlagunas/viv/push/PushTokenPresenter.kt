package cat.xlagunas.viv.push

import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class PushTokenPresenter @Inject
constructor(private val pushTokenRepository: PushTokenRepository) : DisposableViewModel() {

    fun registerToken() {
        disposable.add(
            pushTokenRepository.registerPushToken()
                .subscribe({}, { Timber.e(it, "Error requesting token") })
        )
    }

    fun isPushTokenRegistered() = pushTokenRepository.isPushTokenRegistered()

    fun clearPushToken() = pushTokenRepository.invalidatePushToken()
}