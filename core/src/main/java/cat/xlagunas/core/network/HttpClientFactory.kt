package cat.xlagunas.core.network

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Builds the HTTP stack used by the app, so production and tests share one configuration.
 */
object HttpClientFactory {
    fun okHttpClient(interceptors: List<Interceptor>): OkHttpClient =
        OkHttpClient.Builder()
            .apply { interceptors.forEach { addInterceptor(it) } }
            .build()

    fun retrofit(
        baseUrl: String,
        client: OkHttpClient,
        gson: Gson,
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .client(client)
            .baseUrl(baseUrl)
            .build()
}
