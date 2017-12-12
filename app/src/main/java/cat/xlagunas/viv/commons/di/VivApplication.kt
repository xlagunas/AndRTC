package cat.xlagunas.viv.commons.di

import android.app.Application

class VivApplication : Application() {

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder().withApplication(this).build()
    }
}