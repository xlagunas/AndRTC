package cat.xlagunas.push

import android.annotation.SuppressLint
import javax.inject.Inject
import timber.log.Timber

class PushTokenPresenter @Inject
constructor(private val pushTokenRepository: PushTokenRepository) {

    @SuppressLint("CheckResult")
    fun registerToken() {
        pushTokenRepository.registerPushToken()
            .subscribe({}, { Timber.e(it, "Error requesting token") })
    }

    fun isPushTokenRegistered() = pushTokenRepository.isPushTokenRegistered()

    fun clearPushToken() = pushTokenRepository.invalidatePushToken()
}
