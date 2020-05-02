package cat.xlagunas.core.common

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.core.di.HasViewModelFactory

fun FragmentActivity.viewModelProviderFactory(): ViewModelProvider.Factory {
    return (application as HasViewModelFactory).factory()
}
