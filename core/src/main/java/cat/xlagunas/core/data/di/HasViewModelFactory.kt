package cat.xlagunas.core.data.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider

interface HasViewModelFactory {

    fun factory(): ViewModelProvider.Factory
}

fun FragmentActivity.viewModelProviderFactory(): ViewModelProvider.Factory {
    return (application as HasViewModelFactory).factory()
}

fun Fragment.viewModelProviderFactory(): ViewModelProvider.Factory {
    return requireActivity().viewModelProviderFactory()
}