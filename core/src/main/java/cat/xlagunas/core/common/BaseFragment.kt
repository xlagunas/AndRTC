package cat.xlagunas.core.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.data.di.viewModelProviderFactory

@OpenForTesting
abstract class BaseFragment : Fragment() {
//TODO convert to ext function
    fun <T : ViewModel> viewModel(model: Class<T>): T {
        return ViewModelProviders.of(this, viewModelProviderFactory()).get(model)
    }

}

