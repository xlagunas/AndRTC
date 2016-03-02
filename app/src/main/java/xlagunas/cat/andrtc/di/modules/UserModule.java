package xlagunas.cat.andrtc.di.modules;


import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xlagunas.cat.andrtc.di.PerActivity;
import xlagunas.cat.data.mapper.UserEntityMapper;
import xlagunas.cat.domain.User;
import xlagunas.cat.domain.executor.PostExecutionThread;
import xlagunas.cat.domain.interactor.LoginUseCase;
import xlagunas.cat.domain.interactor.UseCase;
import xlagunas.cat.domain.repository.UserRepository;


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
//    @PerActivity @Named("userList")
//    UseCase provideGetUserListUseCase(
//            GetUserList getUserList) {
//        return getUserList;
//    }

    @Provides
    @PerActivity
    @Named("login")
    UseCase provideGetUserDetailsUseCase(
            UserRepository userRepository, PostExecutionThread postExecutionThread) {
        return new LoginUseCase(postExecutionThread, userRepository);
    }

}
