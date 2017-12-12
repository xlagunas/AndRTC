package cat.xlagunas.viv.commons.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import cat.xlagunas.data.common.preferences.AuthTokenManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideAppContext(app: Application): Context = app

    @Provides
    @Singleton
    fun provideSharedPreference(context: Context): SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAuthToken(sharedPreferences: SharedPreferences) = AuthTokenManagerImpl(sharedPreferences)
}