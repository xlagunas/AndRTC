package cat.xlagunas.andrtc.domain.repository;


import rx.Observable;
import cat.xlagunas.andrtc.domain.User;

/**
 * Created by xlagunas on 20/8/16.
 */
public interface SocialRepository {

    Observable<User> registerGoogleUser();

    Observable<Void> logoutGoogle();
}
