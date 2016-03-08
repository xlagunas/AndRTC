package xlagunas.cat.data.cache;

import rx.Observable;
import xlagunas.cat.data.UserEntity;

/**
 * Created by xlagunas on 8/03/16.
 */
public interface UserCache {

    public boolean isCacheValid();

    public void putUser(UserEntity user);

    public Observable<UserEntity> getUser();

    public void removeCache();
}
