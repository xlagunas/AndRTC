package cat.xlagunas.andrtc.di.modules;


import javax.inject.Named;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.di.UserScope;
import dagger.Module;
import dagger.Provides;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;


/**
 * Created by xlagunas on 2/03/16.
 */

@Module
public class UserModule {

    private User user;

    public UserModule() {}

    public UserModule(User user) {
        this.user = user;
    }

//    @Provides
//    @ActivityScope @Named("userList")
//    UseCase provideGetUserListUseCase(
//            GetUserList getUserList) {
//        return getUserList;
//    }

//    @Provides
//    @ActivityScope
//    @Named("login")
//    UseCase provideGetUserDetailsUseCase(
//            UserRepository userRepository, PostExecutionThread postExecutionThread) {
//        return new LoginUseCase(postExecutionThread, userRepository);
//    }

    @Provides
    @UserScope
    public User getUser(){
        return user;
    }

}
