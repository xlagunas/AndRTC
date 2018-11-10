package cat.xlagunas.core.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import cat.xlagunas.core.data.converter.FriendConverter
import cat.xlagunas.core.data.converter.UserConverter
import cat.xlagunas.core.data.db.FriendDao
import cat.xlagunas.core.data.db.UserDao
import cat.xlagunas.core.data.db.VivDatabase
import cat.xlagunas.core.data.provider.ActivityMonitor
import cat.xlagunas.core.domain.auth.AuthTokenDataStore
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.core.domain.time.TimeProvider
import dagger.BindsInstance
import dagger.Component
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit

@Component(
    modules = [
        ApplicationModule::class,
        NetworkModule::class,
        DatabaseModule::class]
)
interface ApplicationComponent {

    fun userDao(): UserDao
    fun friendDao(): FriendDao
    fun userConverter(): UserConverter
    fun rxSchedulers(): RxSchedulers
    fun authTokenDataStore(): AuthTokenDataStore
    fun vivDabase(): VivDatabase
    fun sharedPreferences(): SharedPreferences
    fun retrofit(): Retrofit
    fun activityMonitor(): ActivityMonitor
    fun friendConverter(): FriendConverter
    fun context(): Context
    fun timeProvider(): TimeProvider

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withApplication(@NotNull app: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(app: VivApplication)
//    fun inject(activity: MainActivity)
//    fun inject(fragment: LoginFragment)
//    fun inject(fragment: RegisterFragment)
//    fun inject(fragment: ContactFragment)
//    fun inject(fragment: ProfileFragment)
//    fun inject(service: PushMessageHandler)
}