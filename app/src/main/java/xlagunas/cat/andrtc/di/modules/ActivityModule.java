package xlagunas.cat.andrtc.di.modules;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import xlagunas.cat.andrtc.di.PerActivity;

/**
 * Created by xlagunas on 2/03/16.
 */
@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides
    @PerActivity
    Activity activity() {
        return this.activity;
    }
}

