package cat.xlagunas.viv.register

import android.content.SharedPreferences
import cat.xlagunas.data.push.PushTokenProviderImpl
import cat.xlagunas.data.user.authentication.AuthenticationApi
import cat.xlagunas.data.user.authentication.AuthenticationRepositoryImpl
import cat.xlagunas.domain.push.PushTokenProvider
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class RegisterModule {

    @Provides
    fun provideRegisterApi(retrofit: Retrofit): AuthenticationApi {
        return retrofit.create(AuthenticationApi::class.java)
    }

    @Provides
    fun providePushTokenProvider(sharedPreferences: SharedPreferences): PushTokenProvider {
        return PushTokenProviderImpl(sharedPreferences)
    }

    @Provides
    fun provideRegisterRepository(authenticationRepository: AuthenticationRepositoryImpl): AuthenticationRepository {
        return authenticationRepository
    }
}