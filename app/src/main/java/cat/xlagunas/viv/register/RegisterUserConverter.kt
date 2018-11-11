package cat.xlagunas.viv.register

import cat.xlagunas.core.domain.entity.User

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