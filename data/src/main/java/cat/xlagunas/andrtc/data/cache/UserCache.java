package cat.xlagunas.andrtc.data.cache;

import java.io.File;

import cat.xlagunas.andrtc.data.UserEntity;
import rx.Observable;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 8/03/16.
 */
public interface UserCache {

    boolean isCacheValid();

    void putUser(UserEntity user);

    Observable<User> getUser();

    void removeCache();

    boolean isGCMRegistered();

    void setGCMRegistrationStatus(boolean status);

    void invalidateCache();

    Observable<File> generateProfilePictureFile();
}
