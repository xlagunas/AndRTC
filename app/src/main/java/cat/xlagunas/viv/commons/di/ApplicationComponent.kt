package cat.xlagunas.viv.commons.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import org.jetbrains.annotations.NotNull

@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withApplication(@NotNull app: Application): Builder

        fun build(): ApplicationComponent
    }
}