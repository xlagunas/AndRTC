package cat.xlagunas.core.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import cat.xlagunas.core.data.auth.AuthTokenPreferenceDataStore
import cat.xlagunas.core.data.time.SystemTimeProvider
import cat.xlagunas.core.domain.auth.AuthTokenDataStore
import cat.xlagunas.core.domain.time.TimeProvider
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class ApplicationModule {

    @Provides
    fun provideAppContext(app: Application): Context = app

    @Provides
    fun provideSharedPreference(context: Context): SharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    @Provides
    fun provideRxSchedulers() = RxSchedulers(Schedulers.io(), AndroidSchedulers.mainThread(), Schedulers.computation())

    @Provides
    fun provideUserMapping() = cat.xlagunas.core.data.converter.UserConverter()

    @Provides
    fun provideFriendMapping() = cat.xlagunas.core.data.converter.FriendConverter()

    @Provides
    fun provideActivityMonitor() = cat.xlagunas.core.data.provider.ActivityMonitor()

    @Provides
    fun provideAuthDataStore(sharedPreferences: SharedPreferences): AuthTokenDataStore =
        AuthTokenPreferenceDataStore(sharedPreferences)

    @Provides
    fun provideTimeProvider(): TimeProvider = SystemTimeProvider()
}