package cat.xlagunas.viv.commons.binding

import android.databinding.BaseObservable
import kotlin.reflect.KProperty


class BindableDelegate<in R : BaseObservable, T : Any>(private var value: T, private val bindingEntry: Int) {
    operator fun getValue(thisRef: R, property: KProperty<*>): T = value

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
        thisRef.notifyPropertyChanged(bindingEntry)
    }


}

fun <R : BaseObservable, T : Any> bindable(value: T, bindingRes: Int): BindableDelegate<R, T> {
    return BindableDelegate(value, bindingRes)
}

