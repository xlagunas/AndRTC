package cat.xlagunas.conference.data

import cat.xlagunas.conference.domain.UserSessionIdentifier
import java.util.UUID
import javax.inject.Inject

class UUIDUserSession @Inject constructor() : UserSessionIdentifier {

    override fun getUserId(): String {
        return sessionID
    }

    private val sessionID by lazy { UUID.randomUUID().toString() }
}