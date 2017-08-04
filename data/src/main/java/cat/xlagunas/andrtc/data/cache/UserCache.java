package cat.xlagunas.andrtc.data.cache;

import java.io.File;

import cat.xlagunas.andrtc.data.UserEntity;
import rx.Observable;
import rx.functions.Action1;
import cat.xlagunas.andrtc.domain.User;

/**
 * Created by xlagunas on 8/03/16.
 */
public interface UserCache {

    boolean isCacheValid();

    void putUser(UserEntity user);

    Observable<User> getUser();

    Action1 removeCache();

    boolean isGCMRegistered();

    void setGCMRegistrationStatus(boolean status);

    void setGCMToken(String token);

    String getGCMToken();

    void invalidateCache();

    Observable<File> generateProfilePictureFile();
}
