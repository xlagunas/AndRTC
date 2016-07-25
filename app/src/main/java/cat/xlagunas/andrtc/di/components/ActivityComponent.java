package cat.xlagunas.andrtc.di.components;


import cat.xlagunas.andrtc.di.ActivityScope;
import cat.xlagunas.andrtc.di.modules.ActivityModule;
import cat.xlagunas.andrtc.view.activity.MainActivity;
import cat.xlagunas.andrtc.view.activity.RegisterActivity;
import cat.xlagunas.andrtc.view.activity.SplashActivity;
import cat.xlagunas.andrtc.view.fragment.LoginFragment;
import dagger.Subcomponent;

/**
 * Created by xlagunas on 2/03/16.
 */

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(LoginFragment fragment);
    void inject(SplashActivity activity);
    void inject(MainActivity activity);
}