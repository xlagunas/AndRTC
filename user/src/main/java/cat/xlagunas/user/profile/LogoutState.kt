package cat.xlagunas.user.profile

sealed class LogoutState {
    data object Idle : LogoutState()

    data object InProgress : LogoutState()

    data object Error : LogoutState()
}
