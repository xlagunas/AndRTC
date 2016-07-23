package cat.xlagunas.andrtc.data.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.net.RestApi;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.data.net.params.LoginParams;
import cat.xlagunas.andrtc.data.net.params.TokenParams;
import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;

import cat.xlagunas.andrtc.data.net.params.UpdateParams;
import okhttp3.Credentials;
import rx.Observable;
import rx.functions.Action1;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 26/02/16.
 */

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private RestApi restApi;
    private UserCache userCache;
    private UserEntityMapper mapper;


    @Inject
    public UserRepositoryImpl(RestApi restApi, UserEntityMapper mapper, UserCache userCache){
        this.restApi = restApi;
        this.mapper = mapper;
        this.userCache = userCache;
    }


    @Override
    public Observable<Friend> searchUsers(User user, String filterName) {
        return restApi.findUsers(Credentials.basic(user.getUsername(), user.getPassword()), filterName)
                .flatMapIterable(userEntities -> userEntities)
                .map(userEntity -> mapper.mapFriendEntity(userEntity));
    }

    public Observable updateProfile(User user){
        return restApi.pullProfile(Credentials.basic(user.getUsername(), user.getPassword()))
                .doOnNext(saveToCacheAction)
                .doOnNext(invalidateCache)
                .flatMap(userEntity -> Observable.empty());
    }

    @Override
    public Observable<Friend> listContacts(User user) {

        return userCache.getUser()
                .flatMapIterable(persistedUser -> persistedUser.getFriends())
                .filter(friend -> friend.getFriendState() == Friend.ACCEPTED);
    }

    @Override
    public Observable<Friend> listRequestedContacts() {
        return userCache.getUser()
                .flatMapIterable(user -> user.getFriends())
                .filter(friend -> friend.getFriendState() == Friend.PENDING);
    }

    @Override
    public Observable<User> login(String username, String password) {
       return restApi.loginUser(new LoginParams(username, password))
               .doOnNext(saveToCacheAction)
               .map(userEntity -> mapper.transformUser(userEntity));
    }

    @Override
    public Observable requestNewFriendship(User user, String id) {
        return restApi.requestFriendship(Credentials.basic(user.getUsername(), user.getPassword()), id)
                .doOnNext(saveToCacheAction)
                .doOnNext(invalidateCache)
                .map(userEntity -> Observable.empty());
    }

    @Override
    public Observable<User> updateFriendship(User user, String id, String previousState, String newState) {
        return restApi.updateFriendship(Credentials.basic(user.getUsername(), user.getPassword()), new UpdateParams(id, previousState, newState))
                .doOnNext(saveToCacheAction)
                .map(userEntity -> mapper.transformUser(userEntity));
    }

    @Override
    public Observable<User> registerUser(User user) {
        UserEntity entity = mapper.tranformUserEntity(user);
        return restApi.createUser(entity)
                .doOnNext(saveToCacheAction)
                .map(userEntity -> mapper.transformUser(userEntity));
    }

    @Override
    public Observable<UserEntity> registerGCMToken(User user, String token) {
        return restApi.addToken(Credentials.basic(user.getUsername(), user.getPassword()), new TokenParams(token))
                .doOnNext(updateTokenAction)
                .flatMap(userEntity -> Observable.empty());
    }

    private final Action1<UserEntity> saveToCacheAction = userEntity -> {
        if (userEntity != null) {
            userCache.putUser(userEntity);
        }
    };

    private final Action1 updateTokenAction = userEntity -> {
            userCache.setGCMRegistrationStatus(true);
    };

    private final Action1 invalidateCache = object -> {
        userCache.invalidateCache();
    };


}
