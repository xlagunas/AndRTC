package xlagunas.cat.data.di.module;

import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import xlagunas.cat.data.mapper.UserEntityMapper;
import xlagunas.cat.data.net.RestApi;
import xlagunas.cat.data.repository.UserRepositoryImpl;
import xlagunas.cat.data.util.DateTimeTypeConverter;
import xlagunas.cat.domain.repository.UserRepository;

/**
 * Created by xlagunas on 29/02/16.
 */

@Module
public class NetworkModule {

    @Provides
    public RestApi getAPIService(OkHttpClient client){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:3000")
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit.create(RestApi.class);
    }

    @Provides
    public OkHttpClient getClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(getLogginInterceptor(HttpLoggingInterceptor.Level.BODY))
                .build();

        return client;
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

    @Provides
    public UserEntityMapper getEntityMapper(){
        return new UserEntityMapper();
    }

    @Provides
    UserRepository getUserRepository(RestApi restApi, UserEntityMapper userEntityMapper){
        return new UserRepositoryImpl(restApi, userEntityMapper);
    }

}
