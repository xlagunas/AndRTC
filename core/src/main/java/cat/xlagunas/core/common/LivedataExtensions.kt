package cat.xlagunas.core.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData as toLiveDataFromPublisher
import io.reactivex.Flowable

fun <T : Any> Flowable<T>.toLiveData(): LiveData<T> {
    return toLiveDataFromPublisher()
}
