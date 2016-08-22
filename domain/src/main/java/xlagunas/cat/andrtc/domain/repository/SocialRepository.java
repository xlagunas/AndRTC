package xlagunas.cat.andrtc.domain.repository;


import java.util.List;

import rx.Observable;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 20/8/16.
 */
public interface SocialRepository {

    Observable<User> registerFacebookUser();
    Observable<List<Friend>> getFacebookFriends();
}
