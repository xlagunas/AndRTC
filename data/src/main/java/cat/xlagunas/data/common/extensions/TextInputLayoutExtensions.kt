package cat.xlagunas.data.common.extensions

import android.support.design.widget.TextInputLayout

fun TextInputLayout.text(): String {
    return this.editText?.text?.toString().orEmpty()
}
