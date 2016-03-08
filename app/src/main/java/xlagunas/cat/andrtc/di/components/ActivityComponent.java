package xlagunas.cat.andrtc.di.components;

import android.app.Activity;

import dagger.Component;
import xlagunas.cat.andrtc.di.PerActivity;
import xlagunas.cat.andrtc.di.modules.ActivityModule;

/**
 * Created by xlagunas on 2/03/16.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    //Exposed to sub-graphs.
    Activity activity();
}