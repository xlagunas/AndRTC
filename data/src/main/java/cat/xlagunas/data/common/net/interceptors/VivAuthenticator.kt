package cat.xlagunas.data.common.net.interceptors

import cat.xlagunas.domain.user.authentication.AuthTokenDataStore
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import dagger.Lazy
import okhttp3.*
import javax.inject.Inject

//class VivAuthenticator @Inject constructor(private val authenticationRepository: Lazy<AuthenticationRepository>) : Authenticator {
//
//    companion object {
//        @JvmStatic
//        val AUTH_HEADER = "Authorization"
//    }
//
//    override fun authenticate(route: Route, response: Response): Request? {
//
//        if (!isAuthHeaderRequired(response.request().url())) {
//            return null
//        }
//
//        if (isAuthenticationHeaderPresent(response)) {
//            authenticationRepository.get().refreshToken().blockingAwait()
//            return response.request().newBuilder().removeHeader(AUTH_HEADER).build()
//        }
//
//        return null
//
//    }
//
//    private fun isAuthenticationHeaderPresent(response: Response): Boolean {
//        return response.request().header(AUTH_HEADER) != null
//    }
//
//    private fun isAuthHeaderRequired(httpUrl: HttpUrl): Boolean {
//        return true
//    }
//}

class AuthHeaderInterceptor @Inject constructor(private val authTokenDataStore: AuthTokenDataStore) : Interceptor {

    companion object {
        @JvmStatic
        val AUTH_HEADER = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain?): Response {

        var request = chain?.request()

        if (authTokenDataStore.isAuthTokenAvailable()) {

            request?.header(AUTH_HEADER)?.isNullOrEmpty()
                    .let {
                        request = chain?.request()!!
                                .newBuilder()
                                .addHeader(AUTH_HEADER, authTokenDataStore.authToken()!!).build()
                    }
        }

        return chain?.proceed(request)!!
    }

}