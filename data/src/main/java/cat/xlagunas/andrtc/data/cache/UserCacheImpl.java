package cat.xlagunas.andrtc.data.cache;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.cache.serializer.JsonSerializer;
import cat.xlagunas.andrtc.data.exception.UserNotFoundException;
import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import rx.Observable;
import rx.Subscriber;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 8/03/16.
 */

@Singleton
public class UserCacheImpl implements UserCache {

    private static final String SETTINGS_FILE_NAME = "cat.xlagunas.andrtc.SETTINGS";
    private final static String CACHE_VALIDATION = "cache_validation";
    private final static String CACHE_TOKEN_GCM = "cache_gcm";
    private final static String CACHE_USER = "cache_user";

    private final Context context;
    private final JsonSerializer serializer;
    private final FileManager fileManager;
    private final UserEntityMapper userEntityMapper;

    @Inject
    public UserCacheImpl(Context context, FileManager fileManager, JsonSerializer serializer, UserEntityMapper userEntityMapper){
        this.context  = context;
        this.fileManager = fileManager;
        this.serializer = serializer;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public boolean isCacheValid() {
        return fileManager.getFromPreferences(context, SETTINGS_FILE_NAME, CACHE_VALIDATION);
    }

    public void invalidateCache() {
        fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, CACHE_VALIDATION, false);
    }

    @Override
    public Observable<File> generateProfilePictureFile() {
            return Observable.create(new Observable.OnSubscribe<File>() {
                @Override
                public void call(Subscriber<? super File> subscriber) {
                    try {
                        File imageFile = fileManager.createImageFile(context);
                        subscriber.onNext(imageFile);
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
            });
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
    public Observable<User> getUser() {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                String serializedUser = fileManager.getStringFromPreferences(context, SETTINGS_FILE_NAME, CACHE_USER);
                if (serializedUser != null) {
                    UserEntity entity = serializer.deserialize(serializedUser);
                    User user = userEntityMapper.transformUser(entity);
                    if (entity != null && user != null) {
                        subscriber.onNext(user);
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

    @Override
    public boolean isGCMRegistered() {
        return fileManager.readFromPreferences(context, SETTINGS_FILE_NAME, CACHE_TOKEN_GCM);
    }

    @Override
    public void setGCMRegistrationStatus(boolean status) {
        fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, CACHE_TOKEN_GCM, status);
    }
}
