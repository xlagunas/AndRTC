package cat.xlagunas.viv.commons.di

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import cat.xlagunas.data.common.provider.ActivityMonitor
import cat.xlagunas.viv.BuildConfig
import com.crashlytics.android.Crashlytics
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

class VivApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var activityMonitor: ActivityMonitor

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        AppInjector.init(this)
        registerActivityLifecycleCallbacks(activityMonitor)
    }

    override fun activityInjector() = dispatchAndroidInjector
}