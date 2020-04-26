package cat.xlagunas.core_navigation

interface Navigator {
    fun startCall(roomId: String)
    fun navigateToContacts()
    fun navigateToProfile()
    fun navigateToLogin()
    fun navigateToRegistration()
}