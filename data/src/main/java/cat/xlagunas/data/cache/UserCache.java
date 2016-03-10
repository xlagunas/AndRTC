package cat.xlagunas.data.cache;

import cat.xlagunas.data.UserEntity;
import rx.Observable;

/**
 * Created by xlagunas on 8/03/16.
 */
public interface UserCache {

    public boolean isCacheValid();

    public void putUser(UserEntity user);

    public Observable<UserEntity> getUser();

    public void removeCache();
}
