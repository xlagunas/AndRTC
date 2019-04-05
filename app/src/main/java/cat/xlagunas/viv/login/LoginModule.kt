package cat.xlagunas.viv.login

import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.data.user.login.GoogleSignInDataSource
import cat.xlagunas.push.PushTokenApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class LoginModule {

    @Provides
    fun provideGoogleSignIn(
        activityMonitor: cat.xlagunas.core.data.provider.ActivityMonitor,
        rxSchedulers: RxSchedulers
    ) =
        GoogleSignInDataSource(activityMonitor, rxSchedulers)

    @Provides
    fun providePushTokenApi(retrofit: Retrofit): PushTokenApi {
        return retrofit.create(PushTokenApi::class.java)
    }
}