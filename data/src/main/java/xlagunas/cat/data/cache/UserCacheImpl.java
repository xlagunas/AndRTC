package xlagunas.cat.data.cache;

import android.content.Context;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import xlagunas.cat.data.UserEntity;
import xlagunas.cat.data.cache.serializer.JsonSerializer;
import xlagunas.cat.data.exception.UserNotFoundException;

/**
 * Created by xlagunas on 8/03/16.
 */

@Singleton
public class UserCacheImpl implements UserCache {

    private static final String SETTINGS_FILE_NAME = "cat.xlagunas.andrtc.SETTINGS";
    private final static String CACHE_VALIDATION = "cache_validation";
    private final static String CACHE_USER = "cache_user";

    private final Context context;
    private final JsonSerializer serializer;
    private final FileManager fileManager;

    @Inject
    public UserCacheImpl(Context context, FileManager fileManager, JsonSerializer serializer){
        this.context  = context;
        this.fileManager = fileManager;
        this.serializer = serializer;
    }

    @Override
    public boolean isCacheValid() {
        return fileManager.getFromPreferences(context, SETTINGS_FILE_NAME, CACHE_VALIDATION);
    }

    @Override
    public void putUser(UserEntity userEntity) {
        if (userEntity != null) {
            String jsonString = serializer.serialize(userEntity);
            fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, CACHE_USER, jsonString);
            fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, CACHE_VALIDATION, true);
        }
    }

    @Override
    public Observable<UserEntity> getUser() {
        return Observable.create(new Observable.OnSubscribe<UserEntity>() {
            @Override
            public void call(Subscriber<? super UserEntity> subscriber) {
                String serializedUser = fileManager.getStringFromPreferences(context, SETTINGS_FILE_NAME, CACHE_USER);
                if (serializedUser != null) {
                    UserEntity entity = serializer.deserialize(serializedUser);
                    if (entity != null) {
                        subscriber.onNext(entity);
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onError(new UserNotFoundException());
                }
            }
        });
    }

    @Override
    public void removeCache() {
        fileManager.clearPreferences(context, SETTINGS_FILE_NAME);
    }
}
