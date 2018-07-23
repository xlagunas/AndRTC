package cat.xlagunas.viv.commons

import android.app.Application
import android.app.Service
import dagger.android.AndroidInjector
import dagger.android.HasServiceInjector

class TestApplication : Application(), HasServiceInjector{

    override fun serviceInjector(): AndroidInjector<Service> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}