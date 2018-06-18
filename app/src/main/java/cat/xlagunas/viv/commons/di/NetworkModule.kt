package cat.xlagunas.viv.commons.di

import android.app.Application
import cat.xlagunas.data.common.net.interceptors.AuthHeaderInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .client(client)
                .baseUrl("http://192.168.0.156:8080")
//                .baseUrl("http://10.239.162.46:8080")
                .build()
    }

    @Provides
    @Singleton
    fun provideOkiHttpClient(application: Application,
//                             authenticator: VivAuthenticator,
                             authInterceptor: AuthHeaderInterceptor): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(ChuckInterceptor(application))
                .addInterceptor(authInterceptor)
//                .authenticator(authenticator)
                .build()
    }
}