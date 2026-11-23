package cat.xlagunas.user.auth

import cat.xlagunas.core.persistence.AuthDataStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthHeaderInterceptor
    @Inject
    constructor(private val authDataStore: AuthDataStore) :
    Interceptor {
        companion object {
            @JvmStatic
            val AUTH_HEADER = "Authorization"
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            if (authDataStore.isAuthTokenAvailable()) {
                if (request.header(AUTH_HEADER).isNullOrEmpty()) {
                    request =
                        chain.request()
                            .newBuilder()
                            .addHeader(AUTH_HEADER, authDataStore.authToken()!!).build()
                }
            }
            return chain.proceed(request)
        }
    }
