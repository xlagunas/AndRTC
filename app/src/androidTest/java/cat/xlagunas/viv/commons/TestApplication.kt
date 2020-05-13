package cat.xlagunas.viv.commons

import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.viv.dagger.VivApplication

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
