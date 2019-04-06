package cat.xlagunas.push

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class PushModule {

    @Provides
    fun providePushTokenRepository(pushTokenRepository: PushTokenRepositoryImpl): PushTokenRepository {
        return pushTokenRepository
    }

    @Provides
    fun providePushTokenProvider(pushTokenProvider: PushTokenProviderImpl): PushTokenProvider {
        return pushTokenProvider
    }

    @Provides
    fun providePushTokenApi(retrofit: Retrofit): PushTokenApi {
        return retrofit.create(PushTokenApi::class.java)
    }
}