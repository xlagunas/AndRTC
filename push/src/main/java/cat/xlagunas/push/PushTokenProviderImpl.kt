package cat.xlagunas.push

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class PushTokenProviderImpl
    @Inject
    constructor(private val sharedPreferences: SharedPreferences) : PushTokenProvider {
        companion object {
            const val PUSH_TOKEN_REGISTERED = "pushTokenRegistered"
        }

        override fun getPushToken() = Tasks.await(FirebaseMessaging.getInstance().token)

        override fun isTokenRegistered() = sharedPreferences.getBoolean(PUSH_TOKEN_REGISTERED, false)

        override fun markTokenAsRegistered() = sharedPreferences.edit { putBoolean(PUSH_TOKEN_REGISTERED, true) }

        override fun invalidatePushToken() {
            Tasks.await(FirebaseMessaging.getInstance().deleteToken())
        }
    }
