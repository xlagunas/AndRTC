package dagger

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.call.CallComponent
import cat.xlagunas.call.CallComponentProvider
import cat.xlagunas.call.CallModule
import cat.xlagunas.core.BuildConfig
import cat.xlagunas.core.data.di.HasViewModelFactory
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import timber.log.Timber

open class VivApplication : Application(), HasViewModelFactory, CallComponentProvider {

    override fun factory(): ViewModelProvider.Factory {
        return appComponent.viewModelProvider()
    }

    val appComponent: ApplicationComponent by lazy {
        initApplicationComponent()
    }

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initApplicationComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder().withApplication(this).build()
    }

    override fun provideCallComponent(): CallComponent {
        return appComponent.callComponent(CallModule())
    }

    companion object {
        @JvmStatic
        fun appComponent(context: Context) =
            (context.applicationContext as VivApplication).appComponent
    }
}