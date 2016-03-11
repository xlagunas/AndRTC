package cat.xlagunas.andrtc.di.components;

import cat.xlagunas.andrtc.di.ActivityScope;
import cat.xlagunas.andrtc.di.modules.ActivityModule;
import cat.xlagunas.andrtc.di.modules.UserModule;
import cat.xlagunas.andrtc.view.fragment.LoginFragment;
import dagger.Component;
import cat.xlagunas.andrtc.view.fragment.RegisterFragment;

/**
 * Created by xlagunas on 2/03/16.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = { ActivityModule.class, UserModule.class})
public interface UserComponent extends ActivityComponent {
    void inject(LoginFragment fragment);
    void inject(RegisterFragment fragment);
}
