package cat.xlagunas.viv.commons.di

import androidx.lifecycle.ViewModelProvider
import dagger.VivApplication

class TestApplication : VivApplication() {

    private lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate() {
        super.onCreate()
    }

    override fun factory(): ViewModelProvider.Factory {
        return viewModelFactory
    }

    fun setViewModelProviderFactory(factory: ViewModelProvider.Factory) {
        viewModelFactory = factory
    }
}