package cat.xlagunas.signaling.data.mapper

import cat.xlagunas.signaling.data.UserSession
import cat.xlagunas.signaling.domain.Session
import com.google.gson.Gson
import javax.inject.Inject

class SessionMapper @Inject constructor(private val gson: Gson) {

    fun convertSession(jsonSession: String): Session {
        return UserSession(jsonSession)
    }

    fun serializeSession(session: Session): String {
        return gson.toJson(session)
    }
}
