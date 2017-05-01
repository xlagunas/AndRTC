package cat.xlagunas.andrtc.data.cache;

import android.content.Context;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.cache.serializer.JsonSerializer;
import cat.xlagunas.andrtc.data.exception.UserNotFoundException;
import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import rx.Observable;
import rx.functions.Action1;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 8/03/16.
 */

@Singleton
public class UserCacheImpl implements UserCache {

    private static final String SETTINGS_FILE_NAME = "cat.xlagunas.andrtc.SETTINGS";
    private final static String CACHE_VALIDATION = "cache_validation";
    private final static String CACHE_TOKEN = "cache_gcm";
    private final static String CACHE_TOKEN_VALUE = "cache_gcm_token";
    private final static String CACHE_USER = "cache_user";

    private final Context context;
    private final JsonSerializer serializer;
    private final FileManager fileManager;
    private final UserEntityMapper userEntityMapper;

    @Inject
    public UserCacheImpl(Context context, FileManager fileManager, JsonSerializer serializer, UserEntityMapper userEntityMapper) {
        this.context = context;
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
        return Observable.fromCallable(() -> fileManager.createImageFile(context));
    }

    @Override
    public void putUser(UserEntity userEntity) {
        if (userEntity != null) {
            userEntity.setPassword(userEntityMapper.encodeBasicAuthPassword(userEntity.getUsername(), userEntity.getPassword()));
            String jsonString = serializer.serialize(userEntity);
            fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, CACHE_USER, jsonString);
            fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, CACHE_VALIDATION, true);
        }
    }

    @Override
    public Observable<User> getUser() {
        return Observable.fromCallable(() -> fileManager.getStringFromPreferences(context, SETTINGS_FILE_NAME, CACHE_USER))
                .map(this::deserializeUser)
                .onErrorResumeNext(error -> Observable.error(new UserNotFoundException("User not persisted")));
    }

    private User deserializeUser(String serializedUser) {
        UserEntity entity = serializer.deserialize(serializedUser);
        entity.setPassword(userEntityMapper.decodeBasicAuthPassword(entity.getUsername(), entity.getPassword()));
        return userEntityMapper.transformUser(entity);
    }

    @Override
    public Action1 removeCache() {
        return action1 -> fileManager.clearPreferences(context, SETTINGS_FILE_NAME);
    }

    @Override
    public boolean isGCMRegistered() {
        return fileManager.readFromPreferences(context, SETTINGS_FILE_NAME, CACHE_TOKEN);
    }

    @Override
    public void setGCMRegistrationStatus(boolean status) {
        fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, CACHE_TOKEN, status);
    }

    @Override
    public void setGCMToken(String token) {
        fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, CACHE_TOKEN_VALUE, token);
    }

    @Override
    public String getGCMToken() {
        return fileManager.getStringFromPreferences(context, SETTINGS_FILE_NAME, CACHE_TOKEN_VALUE);
    }
}
