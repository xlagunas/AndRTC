package cat.xlagunas.data.common.extensions

import android.support.design.widget.TextInputLayout

fun TextInputLayout.text() : String {
    return this.editText?.text?.let { it.toString() }.orEmpty()
}
