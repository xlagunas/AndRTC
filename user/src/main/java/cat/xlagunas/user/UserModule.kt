package cat.xlagunas.user

import cat.xlagunas.user.auth.AuthenticationApi
import cat.xlagunas.user.auth.AuthenticationRepository
import cat.xlagunas.user.auth.AuthenticationRepositoryImpl
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
