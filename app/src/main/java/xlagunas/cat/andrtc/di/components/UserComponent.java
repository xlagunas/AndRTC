package xlagunas.cat.andrtc.di.components;

import dagger.Component;
import xlagunas.cat.andrtc.di.PerActivity;
import xlagunas.cat.andrtc.di.modules.ActivityModule;
import xlagunas.cat.andrtc.di.modules.UserModule;
import xlagunas.cat.andrtc.view.fragment.LoginFragment;
import xlagunas.cat.data.di.module.NetworkModule;

/**
 * Created by xlagunas on 2/03/16.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = { ActivityModule.class, UserModule.class})
public interface UserComponent extends ActivityComponent {
    void inject(LoginFragment fragment);
}
