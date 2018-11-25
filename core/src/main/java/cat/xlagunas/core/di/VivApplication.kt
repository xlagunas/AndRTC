package cat.xlagunas.core.di

import android.app.Application
import android.content.Context
import cat.xlagunas.core.BuildConfig
import com.crashlytics.android.Crashlytics
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

open class VivApplication : Application() {

    @Inject
    lateinit var activityMonitor: cat.xlagunas.core.data.provider.ActivityMonitor

    private val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().withApplication(this).build()
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        Fabric.with(this, Crashlytics())

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