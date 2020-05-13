package cat.xlagunas.user.register

sealed class RegistrationState
    object Success : RegistrationState()
    data class RegistrationError(val message: String?) : RegistrationState()
