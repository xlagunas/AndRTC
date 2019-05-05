package cat.xlagunas.core.di

import android.app.Application
import android.content.Context
import cat.xlagunas.core.BuildConfig
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import timber.log.Timber

open class VivApplication : Application() {

    private val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().withApplication(this).build()
    }

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appComponent(this).inject(this)
    }

    companion object {
        @JvmStatic
        fun appComponent(context: Context) =
            (context.applicationContext as VivApplication).appComponent
    }
}