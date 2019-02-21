package cat.xlagunas.core.data.net.interceptors

import cat.xlagunas.core.domain.auth.AuthTokenDataStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthHeaderInterceptor @Inject constructor(private val authTokenDataStore: AuthTokenDataStore) : Interceptor {

    companion object {
        @JvmStatic
        val AUTH_HEADER = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        if (authTokenDataStore.isAuthTokenAvailable()) {

            request?.header(AUTH_HEADER).isNullOrEmpty()
                .let {
                    request = chain.request()!!
                        .newBuilder()
                        .addHeader(AUTH_HEADER, authTokenDataStore.authToken()!!).build()
                }
        }

        return chain.proceed(request)!!
    }
}