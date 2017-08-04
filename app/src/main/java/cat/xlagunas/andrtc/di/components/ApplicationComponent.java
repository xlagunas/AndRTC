package cat.xlagunas.andrtc.di.components;

import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.data.di.module.NetworkModule;
import cat.xlagunas.andrtc.di.modules.ActivityModule;
import cat.xlagunas.andrtc.di.modules.ApplicationModule;
import cat.xlagunas.andrtc.di.modules.UserModule;
import dagger.Component;

/**
 * Created by xlagunas on 29/02/16.
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

    UserComponent plus(UserModule userModule);

    ActivityComponent plus(ActivityModule activityModule);

    UserCache getUserCache();

}
