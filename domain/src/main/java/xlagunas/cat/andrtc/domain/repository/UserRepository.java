package xlagunas.cat.andrtc.domain.repository;

import java.io.File;

import rx.Observable;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface UserRepository {

    /**
     * AbstractUser search filter
     * @param @link{User} who is authenticated to request data
     * @param filterName String to filter
     * @return All users whose username matches the filter
     */
     Observable<Friend> searchUsers(User user, String filterName);

    /**
     * Return the list of all the contacts
     * @return a list of users
     */
    Observable<Friend> listContacts(User user);

    /**
     * Return the list of all the requested contacts
     * @return a list of users
     */
    Observable<Friend> listRequestedContacts();

    /**
     * Return the user if logged
     * @param username string containing username
     * @param password string containing password
     * @return the logged user
     */
    Observable<User> login(String username, String password);

    Observable requestNewFriendship(User user, String id);

    Observable<User> updateFriendship(User user, String id, String previousState, String newState);

    Observable<User> registerUser(User user);

    Observable<User> updateProfilePicture(User user, String uri);

    Observable registerGCMToken(User user, String token);

    Observable updateProfile(User user);

    Observable requestCallUser(User user, String friendId);

    Observable acceptCallUser(User user, String friendId);

    Observable cancelCallUser(User user, String friendId);


    }
