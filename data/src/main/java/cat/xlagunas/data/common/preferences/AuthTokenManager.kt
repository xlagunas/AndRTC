package cat.xlagunas.data.common.preferences

import android.content.SharedPreferences
import cat.xlagunas.domain.preferences.AuthTokenManager

class AuthTokenManagerImpl(private val sharedPreferences: SharedPreferences) : AuthTokenManager {
    override fun authToken(): String? = sharedPreferences.getString(TOKEN, null)

    override fun isAuthTokenAvailable(): Boolean = sharedPreferences.contains(TOKEN)

    override fun insertAuthToken(token: String) = sharedPreferences.edit().putString(TOKEN, token).apply()

    override fun deleteAuthToken() = sharedPreferences.edit().remove(TOKEN).apply()


    companion object {

        private val TOKEN = "token_preferences"
    }
}
