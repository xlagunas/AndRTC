package xlagunas.cat.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;
import xlagunas.cat.data.UserEntity;
import xlagunas.cat.data.mapper.UserEntityMapper;
import xlagunas.cat.data.net.RestApi;
import xlagunas.cat.domain.AbstractUser;
import xlagunas.cat.domain.Friend;
import xlagunas.cat.domain.User;
import xlagunas.cat.domain.repository.UserRepository;

/**
 * Created by xlagunas on 26/02/16.
 */

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private RestApi restApi;
    private UserEntityMapper mapper;


    @Inject
    public UserRepositoryImpl(RestApi restApi, UserEntityMapper mapper){
        this.restApi = restApi;
        this.mapper = mapper;
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
    public Observable<AbstractUser> login(String username, String password) {
       return restApi.loginUser(username, password).map(new Func1<UserEntity, User>() {
            @Override
            public User call(UserEntity userEntity) {
                return mapper.transformUser(userEntity);
            }
        });
    }

    @Override
    public Observable<List<Friend>> requestNewFriendship(String id) {
        return null;
    }

    @Override
    public Observable<List<Friend>> updateFriendship(String id, String previousState, String newState) {
        return null;
    }
}
