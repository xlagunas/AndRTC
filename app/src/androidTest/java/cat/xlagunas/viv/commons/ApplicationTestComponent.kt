package cat.xlagunas.viv.commons

import android.app.Application
import cat.xlagunas.contact.di.ContactModule
import cat.xlagunas.user.UserModule
import cat.xlagunas.viv.dagger.ApplicationModule
import cat.xlagunas.viv.dagger.DatabaseModule
import cat.xlagunas.viv.dagger.NetworkModule
import cat.xlagunas.viv.dagger.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import org.jetbrains.annotations.NotNull

@Component(
    modules = [
        ApplicationModule::class,
        NetworkModule::class,
        ViewModelModule::class,
        DatabaseModule::class, UserModule::class, ContactModule::class]
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
