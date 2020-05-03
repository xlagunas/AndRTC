package cat.xlagunas.user.login

sealed class LoginState
object SuccessLoginState : LoginState()
data class InvalidLoginState(val errorMessage: String) : LoginState()
object ValidationError : LoginState()
