package cat.xlagunas.ws_messaging.model

import cat.xlagunas.ws_messaging.data.UserSession
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