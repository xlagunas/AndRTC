package cat.xlagunas.viv.commons.di

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

open class VivApplication : Application() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder()
                .withApplication(this)
                .build()
        applicationComponent.inject(this)
    }
}