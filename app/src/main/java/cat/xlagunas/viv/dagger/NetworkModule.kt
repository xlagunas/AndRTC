package cat.xlagunas.viv.dagger

import android.app.Application
import cat.xlagunas.core.BuildConfig
import cat.xlagunas.core.network.HttpClientFactory
import cat.xlagunas.user.auth.AuthHeaderInterceptor
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
class NetworkModule {
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    fun provideRetrofit(
        gson: Gson,
        client: OkHttpClient,
    ): Retrofit {
        return HttpClientFactory.retrofit(BuildConfig.ENDPOINT, client, gson)
    }

    @Provides
    fun provideOkiHttpClient(
        application: Application,
        authInterceptor: AuthHeaderInterceptor,
    ): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        return HttpClientFactory.okHttpClient(
            listOf(
                interceptor,
                ChuckerInterceptor.Builder(application).build(),
                authInterceptor,
            ),
        )
    }
}
