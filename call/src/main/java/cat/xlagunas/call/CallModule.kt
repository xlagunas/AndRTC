package cat.xlagunas.call

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class CallModule {
    @Provides
    fun provideCallRepository(callRepository: CallRepositoryImpl): CallRepository {
        return callRepository
    }

    @Provides
    fun provideCallApi(retrofit: Retrofit): CallApi =
        retrofit.create(CallApi::class.java)
}