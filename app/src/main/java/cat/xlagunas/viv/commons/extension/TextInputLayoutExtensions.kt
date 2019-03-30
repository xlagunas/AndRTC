package cat.xlagunas.viv.commons.extension

fun com.google.android.material.textfield.TextInputLayout.text(): String {
    return this.editText?.text?.toString().orEmpty()
}
