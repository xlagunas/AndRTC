package xlagunas.cat.andrtc.di.components;

import dagger.Component;
import xlagunas.cat.andrtc.di.modules.ApplicationModule;
import xlagunas.cat.andrtc.view.activity.LoginActivity;
import xlagunas.cat.data.di.module.NetworkModule;

/**
 * Created by xlagunas on 29/02/16.
 */

@Component(modules = { ApplicationModule.class, NetworkModule.class })
public interface ApplicationComponent {
    public void inject(LoginActivity activity);
}
