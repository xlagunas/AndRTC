package cat.xlagunas.andrtc.di.components;

import android.app.Activity;

import cat.xlagunas.andrtc.di.ActivityScope;
import cat.xlagunas.andrtc.di.modules.ActivityModule;
import dagger.Component;

/**
 * Created by xlagunas on 2/03/16.
 */

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    //Exposed to sub-graphs.
    Activity activity();
}