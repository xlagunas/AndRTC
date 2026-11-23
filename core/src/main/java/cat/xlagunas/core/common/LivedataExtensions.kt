package cat.xlagunas.core.common

import androidx.lifecycle.LiveData
import io.reactivex.Flowable
import androidx.lifecycle.toLiveData as toLiveDataFromPublisher

fun <T : Any> Flowable<T>.toLiveData(): LiveData<T> {
    return toLiveDataFromPublisher()
}
