package cat.xlagunas.data.common.net.interceptors

import cat.xlagunas.domain.preferences.AuthTokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class VivAuthenticator @Inject constructor(private val authTokenManager: AuthTokenManager) : Authenticator {

    companion object {
        @JvmStatic
        val AUTH_HEADER = "Authorization"
    }

    override fun authenticate(route: Route, response: Response): Request? {
        if (isAuthenticationHeaderPresent(response)) {
            return null
        }

        return authTokenManager.authToken()?.let {
            return response.request().newBuilder().addHeader(AUTH_HEADER, it).build()
        }

    }

    private fun isAuthenticationHeaderPresent(response: Response): Boolean {
        return response.request().header(AUTH_HEADER) != null
    }
}