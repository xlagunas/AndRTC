package cat.xlagunas.signaling.data

import cat.xlagunas.signaling.domain.Session

data class UserSession(private val id: String) :
    Session {
    override fun getId(): String {
        return id
    }
}