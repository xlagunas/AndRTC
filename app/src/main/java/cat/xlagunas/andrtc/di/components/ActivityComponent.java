package cat.xlagunas.andrtc.di.components;


import cat.xlagunas.andrtc.di.ActivityScope;
import cat.xlagunas.andrtc.di.modules.ActivityModule;
import cat.xlagunas.andrtc.view.activity.SplashActivity;
import cat.xlagunas.andrtc.view.fragment.LoginFragment;
import cat.xlagunas.andrtc.view.fragment.RegisterFragment;
import dagger.Subcomponent;

/**
 * Created by xlagunas on 2/03/16.
 */

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(LoginFragment fragment);
    void inject(RegisterFragment fragment);
    void inject(SplashActivity activity);
}