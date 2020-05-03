package cat.xlagunas.core.common

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun <T : ViewModel> Fragment.viewModel(model: Class<T>): T {
    return ViewModelProviders.of(this, viewModelProviderFactory()).get(model)
}

fun Fragment.viewModelProviderFactory(): ViewModelProvider.Factory {
    return requireActivity().viewModelProviderFactory()
}

fun Fragment.displayMessage(message: String) {
    val layout = requireActivity().findViewById<View>(android.R.id.content)
    Snackbar.make(layout, message, BaseTransientBottomBar.LENGTH_LONG).show()
}