package cat.xlagunas.andrtc.data.repository;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import cat.xlagunas.andrtc.data.social.FacebookManager;
import rx.Observable;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.repository.SocialRepository;

/**
 * Created by xlagunas on 20/8/16.
 */

public class SocialRepositoryImpl implements SocialRepository {

    private final FacebookManager facebookManager;
    private final UserEntityMapper entityMapper;

    @Inject
    public SocialRepositoryImpl(FacebookManager facebookManager, UserEntityMapper entityMapper) {
        this.facebookManager = facebookManager;
        this.entityMapper = entityMapper;
    }

    @Override
    public Observable<User> registerFacebookUser() {
        return facebookManager.login()
                .flatMap(accessToken -> facebookManager.requestProfileData(accessToken))
                .map(jsonUserData->entityMapper.parseFacebookJsonData(jsonUserData))
                .map(userEntity -> entityMapper.transformUser(userEntity));
    }
}
