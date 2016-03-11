package cat.xlagunas.andrtc.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import cat.xlagunas.andrtc.data.net.params.LoginParams;
import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.data.net.RestApi;
import cat.xlagunas.andrtc.data.net.params.TokenParams;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;
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

    @Override
    public Observable<Response<Void>> registerGCMToken(User user, String token) {
        return restApi.addToken(user.getHashedPassword(), new TokenParams(token))
                .doOnNext(updateTokenAction);
    }

    private final Action1<UserEntity> saveToCacheAction = userEntity -> {
        if (userEntity != null) {
            userCache.putUser(userEntity);
        }
    };

    private final Action1<Response<Void>> updateTokenAction = response -> {
        if (response.code() == 200) {
            userCache.setGCMRegistrationStatus(true);
        } else {
            userCache.setGCMRegistrationStatus(false);
        }
    };


}
