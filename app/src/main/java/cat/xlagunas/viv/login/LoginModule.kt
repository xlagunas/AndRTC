package cat.xlagunas.viv.login

import cat.xlagunas.data.user.login.GoogleSignInDataSource
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import dagger.Module
import dagger.Provides

@Module
class LoginModule {

    @Provides
    fun provideGoogleSignIn(activityMonitor: cat.xlagunas.core.data.provider.ActivityMonitor, rxSchedulers: RxSchedulers) =
        GoogleSignInDataSource(activityMonitor, rxSchedulers)
}