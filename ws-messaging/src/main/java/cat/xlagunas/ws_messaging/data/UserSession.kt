package cat.xlagunas.ws_messaging.data

import cat.xlagunas.ws_messaging.model.Session

data class UserSession(private val id: String) : Session {
    override fun getId(): String {
        return id
    }
}