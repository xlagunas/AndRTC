package cat.xlagunas.call

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import cat.xlagunas.core.di.ViewModelKey
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

    @Provides
    @IntoMap
    @ViewModelKey(CallViewModel::class)
    fun provideProfileViewModel(callViewModel: CallViewModel): ViewModel {
        return callViewModel
    }
}