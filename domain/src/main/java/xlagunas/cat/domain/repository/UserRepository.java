package xlagunas.cat.domain.repository;

import java.util.List;

import rx.Observable;
import xlagunas.cat.domain.AbstractUser;
import xlagunas.cat.domain.Friend;
import xlagunas.cat.domain.User;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface UserRepository {

    /**
     * AbstractUser search filter
     * @param filterName String to filter
     * @return All users whose username matches the filter
     */
    Observable<List<User>> listUsers(String filterName);

    /**
     * Return the list of all the contacts
     * @return
     */
    Observable<List<Friend>> listContacts();

    /**
     * Return the user if logged
     * @param username
     * @param password
     * @return
     */
    Observable<AbstractUser> login(String username, String password);
    Observable<List<Friend>> requestNewFriendship(String id);
    Observable<List<Friend>> updateFriendship(String id, String previousState, String newState);

}
