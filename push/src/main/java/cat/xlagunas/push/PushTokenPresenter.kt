package cat.xlagunas.push

import android.annotation.SuppressLint
import cat.xlagunas.core.persistence.AuthDataStore
import timber.log.Timber
import javax.inject.Inject

class PushTokenPresenter
    @Inject
    constructor(
        private val pushTokenRepository: PushTokenRepository,
        private val authDataStore: AuthDataStore,
    ) {
        /** Sends a token Firebase just rotated. Logged-out users register theirs on login. */
        @SuppressLint("CheckResult")
        fun onNewToken(token: String) {
            if (!authDataStore.isAuthTokenAvailable()) {
                Timber.d("New push token received while logged out, not registering it")
                return
            }
            pushTokenRepository.registerPushToken(token)
                .subscribe(
                    { Timber.d("Rotated push token registered") },
                    { Timber.e(it, "Error registering rotated push token") },
                )
        }
    }
