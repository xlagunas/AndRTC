package cat.xlagunas.viv.register

import cat.xlagunas.data.common.converter.UserConverter
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.data.user.authentication.AuthenticationApi
import cat.xlagunas.data.user.authentication.AuthenticationRepositoryImpl
import cat.xlagunas.domain.preferences.AuthTokenManager
import cat.xlagunas.domain.schedulers.RxSchedulers
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
    fun provideRegisterRepository(authenticationApi: AuthenticationApi, userDao: UserDao, userConverter: UserConverter, rxSchedulers: RxSchedulers, authTokenManager: AuthTokenManager): AuthenticationRepository {
        return AuthenticationRepositoryImpl(authenticationApi, userDao, userConverter, rxSchedulers, authTokenManager)
    }
}