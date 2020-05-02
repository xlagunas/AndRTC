package cat.xlagunas.user.register

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import cat.xlagunas.user.BR

class RegisterUserBinder : BaseObservable() {

    @get:Bindable
    var firstName by bindable("", BR.firstName)

    @get:Bindable
    var lastName by bindable("", BR.lastName)
    @get:Bindable
    var email by bindable("", BR.email)

    @get:Bindable
    var username by bindable("", BR.username)

    @get:Bindable
    var password by bindable("", BR.password)
}
