package cat.xlagunas.andrtc.data.repository;

import java.util.List;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import cat.xlagunas.andrtc.data.social.FacebookManager;
import rx.Observable;
import rx.schedulers.Schedulers;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.repository.SocialRepository;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

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
                .flatMap(jsonUserData -> entityMapper.parseFacebookJsonData(jsonUserData))
                .map(userEntity -> entityMapper.transformUser(userEntity));
    }

    @Override
    public Observable<List<Friend>> getFacebookFriends() {
        return facebookManager.login()
                .flatMap(accessToken -> facebookManager.requestFriends(accessToken))
                .flatMap(jsonFriendList -> entityMapper.transformFacebookFriends(jsonFriendList))
                .flatMapIterable(userEntities -> userEntities)
                .map(userEntity -> entityMapper.mapFriendEntity(userEntity))
                .toList();

    }

}
