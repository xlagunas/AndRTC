package cat.xlagunas.viv.commons.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import cat.xlagunas.data.common.converter.UserConverter
import cat.xlagunas.data.common.preferences.AuthTokenManagerImpl
import cat.xlagunas.domain.schedulers.RxSchedulers
import cat.xlagunas.data.common.provider.ActivityMonitor
import cat.xlagunas.domain.preferences.AuthTokenManager
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideAppContext(app: Application): Context = app

    @Provides
    @Singleton
    fun provideSharedPreference(context: Context): SharedPreferences =
            context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAuthToken(sharedPreferences: SharedPreferences) : AuthTokenManager = AuthTokenManagerImpl(sharedPreferences)


    @Provides
    @Singleton
    fun provideRxSchedulers() = RxSchedulers(Schedulers.io(), AndroidSchedulers.mainThread(), Schedulers.computation())

    @Provides
    @Singleton
    fun provideUserMapping() = UserConverter()

    @Provides
    @Singleton
    fun provideActivityMonitor() = ActivityMonitor()
}