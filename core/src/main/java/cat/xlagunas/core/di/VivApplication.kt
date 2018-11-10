package cat.xlagunas.core.di

import android.app.Application
import android.content.Context
import cat.xlagunas.core.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class VivApplication : Application() {

    @Inject
    lateinit var activityMonitor: cat.xlagunas.core.data.provider.ActivityMonitor

//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().withApplication(this).build()
    }

    override fun onCreate() {
        super.onCreate()

//        Fabric.with(this, Crashlytics())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appComponent(this).inject(this)

        registerActivityLifecycleCallbacks(activityMonitor)
    }

    companion object {
        @JvmStatic
        fun appComponent(context: Context) =
            (context.applicationContext as VivApplication).appComponent
    }
}