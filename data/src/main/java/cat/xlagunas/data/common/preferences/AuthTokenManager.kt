package cat.xlagunas.data.common.preferences

import android.content.SharedPreferences
import androidx.content.edit
import cat.xlagunas.domain.preferences.AuthTokenManager

class AuthTokenManagerImpl(private val sharedPreferences: SharedPreferences) : AuthTokenManager {

    companion object {
        @JvmStatic
        private val TOKEN = "token_preferences"
    }

    override fun authToken(): String? = sharedPreferences.getString(TOKEN, null)

    override fun isAuthTokenAvailable(): Boolean = sharedPreferences.contains(TOKEN)

    override fun insertAuthToken(token: String) = sharedPreferences.edit { putString(TOKEN, token) }

    override fun deleteAuthToken() = sharedPreferences.edit { remove(TOKEN) }

}
