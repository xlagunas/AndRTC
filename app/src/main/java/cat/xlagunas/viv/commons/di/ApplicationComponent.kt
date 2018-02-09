package cat.xlagunas.viv.commons.di

import android.app.Application
import cat.xlagunas.viv.register.RegisterModule
import dagger.BindsInstance
import dagger.Component
import org.jetbrains.annotations.NotNull
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class, ViewModelModule::class, NetworkModule::class, DatabaseModule::class, RegisterModule::class])
@Singleton
interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withApplication(@NotNull app: Application): Builder
        fun build(): ApplicationComponent

    }

    fun inject(vivApplication: VivApplication)
}