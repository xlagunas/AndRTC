package cat.xlagunas.signaling.data

import cat.xlagunas.signaling.domain.Session
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class SessionAdapter : JsonDeserializer<Session>, JsonSerializer<Session> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Session {
        return UserSession(json.asJsonObject.get("id").asString)
    }

    override fun serialize(
        src: Session,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return context.serialize(src, UserSession::class.java)
    }
}
