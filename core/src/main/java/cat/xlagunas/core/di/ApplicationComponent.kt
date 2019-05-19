package cat.xlagunas.core.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import cat.xlagunas.core.data.converter.FriendConverter
import cat.xlagunas.core.data.converter.UserConverter
import cat.xlagunas.core.data.db.FriendDao
import cat.xlagunas.core.data.db.VivDatabase
import cat.xlagunas.core.domain.auth.AuthDataStore
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.core.domain.time.TimeProvider
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit

@Component(
    modules = [
        ApplicationModule::class,
        NetworkModule::class,
        DatabaseModule::class]
)
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
    fun notificationManager() : NotificationManagerCompat

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withApplication(@NotNull app: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(app: VivApplication)
}