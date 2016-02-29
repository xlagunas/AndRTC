package xlagunas.cat.andrtc.di.components;

import dagger.Component;
import xlagunas.cat.andrtc.di.modules.ApplicationModule;
import xlagunas.cat.andrtc.di.modules.NetworkModule;

/**
 * Created by xlagunas on 29/02/16.
 */

@Component(modules = { ApplicationModule.class, NetworkModule.class })
public interface ApplicationComponent {

}
