package cat.xlagunas.core.di

import androidx.lifecycle.ViewModelProvider

interface HasViewModelFactory {

    fun factory(): ViewModelProvider.Factory
}
