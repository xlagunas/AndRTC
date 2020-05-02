package cat.xlagunas.core.navigation

interface Navigator {
    fun requestCall(userId: Long, contactName: String)
    fun startCall(roomId: String)
    fun navigateToContacts()
    fun navigateToProfile()
    fun navigateToLogin()
    fun navigateToRegistration()
}
