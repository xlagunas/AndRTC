package cat.xlagunas.call

import androidx.lifecycle.ViewModel
import cat.xlagunas.push.MessageProcessor
import cat.xlagunas.push.MessageType
import cat.xlagunas.push.PushMessageProcessorKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import di.ViewModelKey
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

    @Provides
    @IntoMap
    @PushMessageProcessorKey(MessageType.CREATE_CALL)
    fun provideCallsMessageProcessor(callMessageProcessor: CallMessageProcessor): MessageProcessor =
        callMessageProcessor
}