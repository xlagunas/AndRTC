package cat.xlagunas.core.data.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import cat.xlagunas.core.domain.auth.AuthDataStore
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class AuthPreferenceDataStore @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AuthDataStore {

    companion object {
        private const val TOKEN = "token_preferences"
        private const val USER_ID = "current_user_preference"
        private val USER_ID_WRAPPER = BehaviorSubject.create<Long>()
    }

    override fun authToken(): String? = sharedPreferences.getString(TOKEN, null)

    override fun isAuthTokenAvailable(): Boolean = sharedPreferences.contains(TOKEN)

    override fun insertAuthToken(token: String) = sharedPreferences.edit { putString(TOKEN, token) }

    override fun deleteAuthToken() = sharedPreferences.edit { remove(TOKEN) }

    override fun updateCurrentUserId(userId: Long) = sharedPreferences.edit {
        putLong(USER_ID, userId)
        USER_ID_WRAPPER.onNext(userId)
    }

    override fun getCurrentUserId(): Long = sharedPreferences.getLong(USER_ID, -1)

    override fun getCurrentUserIdFlowable(): Flowable<Long> {
        return USER_ID_WRAPPER.toFlowable(BackpressureStrategy.LATEST)
    }
}