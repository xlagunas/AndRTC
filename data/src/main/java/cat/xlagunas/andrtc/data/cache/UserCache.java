package cat.xlagunas.andrtc.data.cache;

import cat.xlagunas.andrtc.data.UserEntity;
import rx.Observable;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 8/03/16.
 */
public interface UserCache {

    public boolean isCacheValid();

    public void putUser(UserEntity user);

    public Observable<User> getUser();

    public void removeCache();
}
