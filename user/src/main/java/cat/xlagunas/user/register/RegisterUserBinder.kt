package cat.xlagunas.user.register

data class RegisterUserBinder(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var username: String = "",
    var password: String = ""
)
