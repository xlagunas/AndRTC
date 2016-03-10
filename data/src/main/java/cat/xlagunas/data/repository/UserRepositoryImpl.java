package cat.xlagunas.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.data.UserEntity;
import cat.xlagunas.data.cache.UserCache;
import cat.xlagunas.data.mapper.UserEntityMapper;
import cat.xlagunas.data.net.RestApi;
import cat.xlagunas.data.net.params.LoginParams;
import rx.Observable;
import rx.functions.Action1;
import xlagunas.cat.domain.Friend;
import xlagunas.cat.domain.User;
import xlagunas.cat.domain.repository.UserRepository;

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
    public Observable<List<User>> listUsers(String filterName) {
        return null;
    }

    @Override
    public Observable<List<Friend>> listContacts() {
        return null;
    }

    @Override
    public Observable<User> login(String username, String password) {
       return restApi.loginUser(new LoginParams(username, password))
               .doOnNext(saveToCacheAction)
               .map(userEntity -> mapper.transformUser(userEntity));
    }

    @Override
    public Observable<List<Friend>> requestNewFriendship(String id) {
        return null;
    }

    @Override
    public Observable<List<Friend>> updateFriendship(String id, String previousState, String newState) {
        return null;
    }

    @Override
    public Observable<User> registerUser(User user) {
        UserEntity entity = mapper.tranformUserEntity(user);
        return restApi.createUser(entity)
                .doOnNext(saveToCacheAction)
                .map(userEntity -> mapper.transformUser(userEntity));
    }

    private final Action1<UserEntity> saveToCacheAction = userEntity -> {
        if (userEntity != null) {
            userCache.putUser(userEntity);
        }
    };
}
