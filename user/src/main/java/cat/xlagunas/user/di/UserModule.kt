package cat.xlagunas.user.di

import cat.xlagunas.user.data.AuthenticationApi
import cat.xlagunas.user.data.AuthenticationRepositoryImpl
import cat.xlagunas.user.domain.AuthenticationRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class UserModule {

    @Provides
    fun provideRegisterApi(retrofit: Retrofit): AuthenticationApi {
        return retrofit.create(AuthenticationApi::class.java)
    }

    @Provides
    fun provideRegisterRepository(authenticationRepository: AuthenticationRepositoryImpl): AuthenticationRepository {
        return authenticationRepository
    }
}