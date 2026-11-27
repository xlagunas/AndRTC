package cat.xlagunas.viv.dagger

import cat.xlagunas.core.network.BaseUrl
import cat.xlagunas.push.PushTokenProvider
import cat.xlagunas.viv.BuildConfig
import cat.xlagunas.viv.e2e.E2ePushTokenProvider
import dagger.Module
import dagger.Provides

@Module
class VariantModule {
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.E2E_BACKEND_URL

    @Provides
    fun providePushTokenProvider(pushTokenProvider: E2ePushTokenProvider): PushTokenProvider {
        return pushTokenProvider
    }
}
