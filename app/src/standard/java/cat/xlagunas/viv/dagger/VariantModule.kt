package cat.xlagunas.viv.dagger

import cat.xlagunas.core.BuildConfig
import cat.xlagunas.core.network.BaseUrl
import cat.xlagunas.push.PushTokenProvider
import cat.xlagunas.push.PushTokenProviderImpl
import dagger.Module
import dagger.Provides

@Module
class VariantModule {
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.ENDPOINT

    @Provides
    fun providePushTokenProvider(pushTokenProvider: PushTokenProviderImpl): PushTokenProvider {
        return pushTokenProvider
    }
}
