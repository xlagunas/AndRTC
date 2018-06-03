package cat.xlagunas.data.user.authentication

import android.content.SharedPreferences
import androidx.core.content.edit
import cat.xlagunas.domain.user.authentication.AuthTokenDataStore
import javax.inject.Inject

class AuthTokenPreferenceDataStore @Inject constructor(
        private val sharedPreferences: SharedPreferences) : AuthTokenDataStore {

    companion object {
        @JvmStatic
        private val TOKEN = "token_preferences"
    }


    override fun authToken(): String? = sharedPreferences.getString(TOKEN, null)

    override fun isAuthTokenAvailable(): Boolean = sharedPreferences.contains(TOKEN)

    override fun insertAuthToken(token: String) = sharedPreferences.edit { putString(TOKEN, token) }

    override fun deleteAuthToken() = sharedPreferences.edit { remove(TOKEN) }

}