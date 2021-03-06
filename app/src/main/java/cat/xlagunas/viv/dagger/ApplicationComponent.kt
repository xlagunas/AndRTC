package cat.xlagunas.viv.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.call.CallComponent
import cat.xlagunas.call.CallModule
import cat.xlagunas.contact.data.FriendConverter
import cat.xlagunas.contact.di.ContactModule
import cat.xlagunas.contact.domain.TimeProvider
import cat.xlagunas.core.persistence.AuthDataStore
import cat.xlagunas.core.persistence.db.FriendDao
import cat.xlagunas.core.persistence.db.VivDatabase
import cat.xlagunas.core.scheduler.RxSchedulers
import cat.xlagunas.push.PushModule
import cat.xlagunas.user.UserConverter
import cat.xlagunas.user.UserModule
import cat.xlagunas.viv.push.PushMessageHandler
import cat.xlagunas.viv.push.PushMessageProcessorsModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit

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
