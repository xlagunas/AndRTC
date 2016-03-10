package cat.xlagunas.andrtc;

import android.app.Application;
import android.content.Context;

import cat.xlagunas.andrtc.di.components.ApplicationComponent;
import cat.xlagunas.andrtc.di.modules.ApplicationModule;
import cat.xlagunas.andrtc.di.components.DaggerApplicationComponent;

/**
 * Created by xlagunas on 29/02/16.
 */
public class CustomApplication extends Application{

    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupGraph();
    }

    private void setupGraph() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

    }

    public static CustomApplication getApp(Context context) {
        return (CustomApplication) context.getApplicationContext();
    }

    public ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }
}
