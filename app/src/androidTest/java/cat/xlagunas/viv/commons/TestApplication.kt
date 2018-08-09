package cat.xlagunas.viv.commons

import android.app.Application
import android.app.Service
import cat.xlagunas.viv.commons.di.DaggerApplicationTestComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasServiceInjector
import javax.inject.Inject

class TestApplication : Application(), HasServiceInjector {

    @Inject
    lateinit var dispatchServiceInject: DispatchingAndroidInjector<Service>

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationTestComponent.builder().withApplication(this).build().inject(this)
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return dispatchServiceInject
    }
}