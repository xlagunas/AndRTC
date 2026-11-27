package cat.xlagunas.viv.e2e

import android.content.SharedPreferences
import androidx.core.content.edit
import cat.xlagunas.push.PushTokenProvider
import javax.inject.Inject

/** Push token provider that needs neither Google Play services nor Firebase. */
class E2ePushTokenProvider
    @Inject
    constructor(private val sharedPreferences: SharedPreferences) : PushTokenProvider {
        override fun getPushToken(): String = PUSH_TOKEN

        override fun isTokenRegistered() = sharedPreferences.getBoolean(PUSH_TOKEN_REGISTERED, false)

        override fun markTokenAsRegistered() = sharedPreferences.edit { putBoolean(PUSH_TOKEN_REGISTERED, true) }

        override fun clearTokenRegistration() = sharedPreferences.edit { remove(PUSH_TOKEN_REGISTERED) }

        override fun invalidatePushToken() = clearTokenRegistration()

        private companion object {
            const val PUSH_TOKEN = "e2e-push-token"
            const val PUSH_TOKEN_REGISTERED = "pushTokenRegistered"
        }
    }
