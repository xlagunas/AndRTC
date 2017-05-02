package cat.xlagunas.andrtc.data.repository;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import cat.xlagunas.andrtc.data.social.GoogleManager;
import rx.Observable;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.repository.SocialRepository;

/**
 * Created by xlagunas on 20/8/16.
 */

public class SocialRepositoryImpl implements SocialRepository {

    private final GoogleManager googleManager;
    private final UserEntityMapper entityMapper;

    @Inject
    public SocialRepositoryImpl(GoogleManager googleManager, UserEntityMapper entityMapper) {
        this.googleManager = googleManager;
        this.entityMapper = entityMapper;
    }

    @Override
    public Observable<User> registerGoogleUser() {
        return googleManager.login()
                .flatMap(accountDetails -> entityMapper.parseGoogleData(accountDetails))
                .map(userEntity -> entityMapper.transformUser(userEntity));
    }

    @Override
    public Observable<Void> logoutGoogle() {
        return null;
    }

}
