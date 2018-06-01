package cat.xlagunas.viv.commons.di

import android.app.Application
import cat.xlagunas.viv.contact.ContactModule
import cat.xlagunas.viv.register.RegisterModule
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class,
    ViewModelModule::class,
    NetworkModuleTest::class,
    DatabaseModule::class,
    RegisterModule::class,
    ContactModule::class])
@Singleton
interface ApplicationTestComponent : ApplicationComponent {
    fun client(): OkHttpClient
    fun retrofit(): Retrofit

    override fun inject(vivApplication: VivApplication)

    @Component.Builder
    interface Builder {


        @BindsInstance
        fun withApplication(@NotNull app: Application): Builder

        fun build(): ApplicationTestComponent

    }
}