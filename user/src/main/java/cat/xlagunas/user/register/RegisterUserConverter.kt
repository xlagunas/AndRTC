package cat.xlagunas.user.register

import cat.xlagunas.user.User

class RegisterUserConverter {

    fun toUser(registerUserBinder: RegisterUserBinder) =
        User(
            registerUserBinder.username,
            registerUserBinder.firstName,
            registerUserBinder.lastName,
            registerUserBinder.email,
            "",
            registerUserBinder.password
        )
}
