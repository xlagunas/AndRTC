package cat.xlagunas.core.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

fun <T : ViewModel> Fragment.viewModel(model: Class<T>): T {
    return ViewModelProviders.of(this, viewModelProviderFactory()).get(model)
}

fun Fragment.viewModelProviderFactory(): ViewModelProvider.Factory {
    return requireActivity().viewModelProviderFactory()
}