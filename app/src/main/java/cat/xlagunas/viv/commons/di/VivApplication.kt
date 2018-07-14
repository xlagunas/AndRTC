package cat.xlagunas.viv.commons.di

import android.arch.lifecycle.ViewModelProvider
import cat.xlagunas.data.common.provider.ActivityMonitor
import cat.xlagunas.viv.BuildConfig
import com.crashlytics.android.Crashlytics
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

open class VivApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .withApplication(this)
            .build()
        return applicationComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var activityMonitor: ActivityMonitor

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        registerActivityLifecycleCallbacks(activityMonitor)
    }
}