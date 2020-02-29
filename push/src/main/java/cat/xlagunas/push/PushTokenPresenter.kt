package cat.xlagunas.push

import android.annotation.SuppressLint
import timber.log.Timber
import javax.inject.Inject

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