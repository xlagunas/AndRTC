package dagger

import android.annotation.TargetApi
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import cat.xlagunas.core.common.PushUtils.CALL_CHANNEL_ID
import cat.xlagunas.core.data.auth.AuthPreferenceDataStore
import cat.xlagunas.core.data.time.SystemTimeProvider
import cat.xlagunas.core.domain.auth.AuthDataStore
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.core.domain.time.TimeProvider
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
    fun provideRxSchedulers() =
        RxSchedulers(Schedulers.io(), AndroidSchedulers.mainThread(), Schedulers.computation())

    @Provides
    fun provideUserMapping() = cat.xlagunas.core.data.converter.UserConverter()

    @Provides
    fun provideFriendMapping() = cat.xlagunas.core.data.converter.FriendConverter()

    @Provides
    fun provideAuthDataStore(sharedPreferences: SharedPreferences): AuthDataStore =
        AuthPreferenceDataStore(sharedPreferences)

    @Provides
    fun provideTimeProvider(): TimeProvider = SystemTimeProvider()

    @Provides
    fun provideNotificationManager(context: Context): NotificationManagerCompat {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        return NotificationManagerCompat.from(context)
    }

    @TargetApi(26)
    private fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val callNotificationChannel = NotificationChannel(
            CALL_CHANNEL_ID,
            "Call notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(callNotificationChannel)
    }
}