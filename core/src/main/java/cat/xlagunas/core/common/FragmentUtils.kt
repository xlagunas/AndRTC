package cat.xlagunas.core.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import cat.xlagunas.core.data.di.viewModelProviderFactory

fun <T : ViewModel> Fragment.viewModel(model: Class<T>): T {
    return ViewModelProviders.of(this, viewModelProviderFactory()).get(model)
}


