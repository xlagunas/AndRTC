package cat.xlagunas.viv.commons

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class DisposableViewModel : ViewModel() {

    protected val disposable = CompositeDisposable()

    override fun onCleared() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}