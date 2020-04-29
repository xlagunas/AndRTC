package dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.call.CallComponent
import cat.xlagunas.call.CallModule
import cat.xlagunas.contact.di.ContactModule
import cat.xlagunas.core.data.converter.FriendConverter
import cat.xlagunas.core.data.converter.UserConverter
import cat.xlagunas.core.data.db.FriendDao
import cat.xlagunas.core.data.db.VivDatabase
import cat.xlagunas.core.domain.auth.AuthDataStore
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.core.domain.time.TimeProvider
import cat.xlagunas.push.PushModule
import cat.xlagunas.user.di.UserModule
import cat.xlagunas.viv.commons.di.ViewModelModule
import cat.xlagunas.viv.push.PushMessageHandler
import cat.xlagunas.viv.push.PushMessageProcessorsModule
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit
import javax.inject.Singleton

@Component(
    modules = [
        ApplicationModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        ViewModelModule::class,
        ContactModule::class,
        UserModule::class,
        PushModule::class,
        ContactModule::class,
        PushMessageProcessorsModule::class]
)
@Singleton
interface ApplicationComponent {

    fun friendDao(): FriendDao
    fun userConverter(): UserConverter
    fun rxSchedulers(): RxSchedulers
    fun authTokenDataStore(): AuthDataStore
    fun vivDabase(): VivDatabase
    fun sharedPreferences(): SharedPreferences
    fun retrofit(): Retrofit
    fun friendConverter(): FriendConverter
    fun okHttpClient(): OkHttpClient
    fun context(): Context
    fun application(): Application
    fun timeProvider(): TimeProvider
    fun notificationManager(): NotificationManagerCompat
    fun viewModelProvider(): ViewModelProvider.Factory

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withApplication(@NotNull app: Application): Builder

        fun build(): ApplicationComponent
    }

    fun callComponent(callModule: CallModule): CallComponent

    fun inject(pushMessageHandler: PushMessageHandler)
}