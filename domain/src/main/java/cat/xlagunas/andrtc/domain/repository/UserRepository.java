package cat.xlagunas.andrtc.domain.repository;

import rx.Observable;
import cat.xlagunas.andrtc.domain.Friend;
import cat.xlagunas.andrtc.domain.User;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface UserRepository {

    Observable<Friend> searchUsers(User user, String filterName);

    /**
     * Return the list of all the contacts except the non Requested
     *
     * @return a list of users
     */
    Observable<Friend> listContacts(User user);

    /**
     * Return the list of all the requested contacts
     *
     * @return a list of users
     */
    Observable<Friend> listRequestedContacts();

    Observable<Friend> listAllContacts();

    /**
     * Return the user if logged
     *
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

    Observable<User> registerFacebookUser(User user);

    Observable<User> registerGoogleUser(User user);

    Observable<Void> logoutUser();

    Observable<String> observeDataInvalidation();

}
