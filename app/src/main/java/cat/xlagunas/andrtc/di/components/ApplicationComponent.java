package cat.xlagunas.andrtc.di.components;

import javax.inject.Singleton;

import cat.xlagunas.andrtc.di.modules.ActivityModule;
import cat.xlagunas.andrtc.di.modules.UserModule;
import cat.xlagunas.andrtc.gcm.MyGcmListenerService;
import dagger.Component;
import cat.xlagunas.andrtc.di.modules.ApplicationModule;
import cat.xlagunas.andrtc.data.di.module.NetworkModule;

/**
 * Created by xlagunas on 29/02/16.
 */

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

    UserComponent plus(UserModule userModule);
    ActivityComponent plus(ActivityModule activityModule);

    public void inject(MyGcmListenerService service);
}
