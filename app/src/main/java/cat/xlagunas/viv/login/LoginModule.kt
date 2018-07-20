package cat.xlagunas.viv.login

import cat.xlagunas.data.common.provider.ActivityMonitor
import cat.xlagunas.data.user.login.GoogleSignInDataSource
import cat.xlagunas.domain.schedulers.RxSchedulers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LoginModule {

    @Provides
    @Singleton
    fun provideGoogleSignIn(activityMonitor: ActivityMonitor, rxSchedulers: RxSchedulers) =
        GoogleSignInDataSource(activityMonitor, rxSchedulers)
}