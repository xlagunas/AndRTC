package cat.xlagunas.test.utils

import cat.xlagunas.core.navigation.Navigator

sealed interface NavigationCall {
    data class RequestCall(val userId: Long, val contactName: String) : NavigationCall

    data class StartCall(val roomId: String) : NavigationCall

    data object ToContacts : NavigationCall

    data object ToProfile : NavigationCall

    data object ToLogin : NavigationCall

    data object ToRegistration : NavigationCall
}

/**
 * Navigator fake that records every navigation request in order.
 */
class RecordingNavigator : Navigator {
    val calls = StateRecorder<NavigationCall>()

    override fun requestCall(
        userId: Long,
        contactName: String,
    ) = calls.record(NavigationCall.RequestCall(userId, contactName))

    override fun startCall(roomId: String) = calls.record(NavigationCall.StartCall(roomId))

    override fun navigateToContacts() = calls.record(NavigationCall.ToContacts)

    override fun navigateToProfile() = calls.record(NavigationCall.ToProfile)

    override fun navigateToLogin() = calls.record(NavigationCall.ToLogin)

    override fun navigateToRegistration() = calls.record(NavigationCall.ToRegistration)
}
