package cat.xlagunas.viv.commons.di

import android.app.Application
import cat.xlagunas.core.di.ApplicationModule
import cat.xlagunas.core.di.DatabaseModule
import cat.xlagunas.core.di.NetworkModule
import cat.xlagunas.viv.commons.TestApplication
import cat.xlagunas.viv.contact.ContactModule
import cat.xlagunas.viv.login.LoginModule
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
interface ApplicationTestComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withApplication(@NotNull app: Application): Builder

        fun build(): ApplicationTestComponent
    }

    fun inject(app: TestApplication)
}