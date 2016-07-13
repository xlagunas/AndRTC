package cat.xlagunas.andrtc.di.modules;


import cat.xlagunas.andrtc.di.UserScope;
import dagger.Module;
import dagger.Provides;
import xlagunas.cat.andrtc.domain.User;


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
