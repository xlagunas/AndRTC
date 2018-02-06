package cat.xlagunas.viv.register

import cat.xlagunas.data.common.converter.UserConverter
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.data.user.register.RegisterApi
import cat.xlagunas.data.user.register.RegisterRepositoryImpl
import cat.xlagunas.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.user.register.RegisterRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class RegisterModule {

    @Provides
    fun provideRegisterApi(retrofit: Retrofit): RegisterApi {
        return retrofit.create(RegisterApi::class.java)
    }

    @Provides
    fun provideRegisterRepository(registerApi: RegisterApi, userDao: UserDao, userConverter: UserConverter, rxSchedulers: RxSchedulers): RegisterRepository {
        return RegisterRepositoryImpl(registerApi, userDao, userConverter, rxSchedulers)
    }
}