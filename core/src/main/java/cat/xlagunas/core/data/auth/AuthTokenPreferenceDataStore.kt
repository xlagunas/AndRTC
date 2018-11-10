package cat.xlagunas.core.data.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import cat.xlagunas.core.domain.auth.AuthTokenDataStore
import javax.inject.Inject

class AuthTokenPreferenceDataStore @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AuthTokenDataStore {

    companion object {
        @JvmStatic
        private val TOKEN = "token_preferences"
    }

    override fun authToken(): String? = sharedPreferences.getString(TOKEN, null)

    override fun isAuthTokenAvailable(): Boolean = sharedPreferences.contains(TOKEN)

    override fun insertAuthToken(token: String) = sharedPreferences.edit { putString(TOKEN, token) }

    override fun deleteAuthToken() = sharedPreferences.edit { remove(TOKEN) }
}