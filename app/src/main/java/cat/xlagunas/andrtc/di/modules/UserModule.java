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

    @Provides
    @UserScope
    public User getUser(){
        return user;
    }

}
