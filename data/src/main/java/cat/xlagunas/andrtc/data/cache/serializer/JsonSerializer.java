package cat.xlagunas.andrtc.data.cache.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.util.DateTimeTypeConverter;

@Singleton
public class JsonSerializer {

    private final Gson gson;

    @Inject
    public JsonSerializer() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter());
        gson = builder.create();
    }

    /**
     * Serialize an object to Json.
     *
     * @param userEntity {@link UserEntity} to serialize.
     */
    public String serialize(UserEntity userEntity) {
        String jsonString = gson.toJson(userEntity, UserEntity.class);
        return jsonString;
    }

    /**
     * Deserialize a json representation of an object.
     *
     * @param jsonString A json string to deserialize.
     * @return {@link UserEntity}
     */
    public UserEntity deserialize(String jsonString) {
        UserEntity userEntity = gson.fromJson(jsonString, UserEntity.class);
        return userEntity;
    }
}
