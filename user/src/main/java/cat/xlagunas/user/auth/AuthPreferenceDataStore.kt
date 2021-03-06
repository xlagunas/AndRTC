package cat.xlagunas.user.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import cat.xlagunas.core.persistence.AuthDataStore
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class AuthPreferenceDataStore @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AuthDataStore {

    private val userIdSubject = BehaviorSubject.createDefault(getCurrentUserId())

    companion object {
        private const val TOKEN = "token_preferences"
        private const val USER_ID = "current_user_preference"
    }

    override fun authToken(): String? = sharedPreferences.getString(TOKEN, null)

    override fun isAuthTokenAvailable(): Boolean = sharedPreferences.contains(TOKEN)

    override fun insertAuthToken(token: String) = sharedPreferences.edit { putString(
        TOKEN, token) }

    override fun deleteAuthToken() = sharedPreferences.edit { remove(TOKEN) }

    override fun updateCurrentUserId(userId: Long) = sharedPreferences.edit {
        putLong(USER_ID, userId)
        userIdSubject.onNext(userId)
    }

    override fun getCurrentUserId(): Long = sharedPreferences.getLong(USER_ID, -1)

    override fun getCurrentUserIdFlowable(): BehaviorSubject<Long> {
        return userIdSubject
    }

    override fun deleteUserPreferences() {
        sharedPreferences.edit { remove(USER_ID) }
        userIdSubject.onNext(-1)
    }
}
