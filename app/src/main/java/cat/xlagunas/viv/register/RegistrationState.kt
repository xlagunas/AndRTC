package cat.xlagunas.viv.register

sealed class RegistrationState
    object Success : RegistrationState()
    data class RegistrationError(val message: String?): RegistrationState()
