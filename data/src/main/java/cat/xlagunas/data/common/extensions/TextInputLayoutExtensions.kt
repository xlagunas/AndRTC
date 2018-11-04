package cat.xlagunas.data.common.extensions

import com.google.android.material.textfield.TextInputLayout

fun com.google.android.material.textfield.TextInputLayout.text(): String {
    return this.editText?.text?.toString().orEmpty()
}
