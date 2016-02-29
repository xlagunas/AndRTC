package xlagunas.cat.andrtc.di.modules;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xlagunas.cat.data.net.RestApi;

/**
 * Created by xlagunas on 29/02/16.
 */

@Module
public class NetworkModule {

    @Provides
    @Singleton
    public RestApi getAPIService(OkHttpClient client){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(RestApi.class);
    }

    @Provides @Singleton
    public OkHttpClient getClient(List<Interceptor> interceptors){
        OkHttpClient client = new OkHttpClient();
        client.interceptors().addAll(interceptors);

        return client;
    }

    @Provides @Singleton
    public List<Interceptor> getInterceptors(){

        return Arrays.asList(getLogginInterceptor(HttpLoggingInterceptor.Level.BODY));
    }

    private Interceptor getAuthInterceptor(final String username, final String password){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
//                        .header("Authorization", Credentials.basic(username, password))
                        .header("Accept", "applicaton/json")
//                        .header("X-Requested-With", "com.free.basquetcat")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    private Interceptor getLogginInterceptor(HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }


}
