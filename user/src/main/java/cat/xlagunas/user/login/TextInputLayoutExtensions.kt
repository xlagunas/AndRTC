package cat.xlagunas.user.login

fun com.google.android.material.textfield.TextInputLayout.text(): String {
    return this.editText?.text?.toString().orEmpty()
}
