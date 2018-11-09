package cat.xlagunas.viv.commons.di

import android.app.Application
import cat.xlagunas.viv.contact.ContactFragment
import cat.xlagunas.viv.contact.ContactModule
import cat.xlagunas.viv.landing.MainActivity
import cat.xlagunas.viv.login.LoginFragment
import cat.xlagunas.viv.login.LoginModule
import cat.xlagunas.viv.profile.ProfileFragment
import cat.xlagunas.viv.push.PushMessageHandler
import cat.xlagunas.viv.register.RegisterFragment
import cat.xlagunas.viv.register.RegisterModule
import dagger.BindsInstance
import dagger.Component
import org.jetbrains.annotations.NotNull
import javax.inject.Singleton

@Component(
    modules = [
        ApplicationModule::class,
        NetworkModule::class,
        ViewModelModule::class,
        DatabaseModule::class, RegisterModule::class, LoginModule::class, ContactModule::class]
)
@Singleton
interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withApplication(@NotNull app: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(app: VivApplication)
    fun inject(activity: MainActivity)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: RegisterFragment)
    fun inject(fragment: ContactFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(service: PushMessageHandler)
}