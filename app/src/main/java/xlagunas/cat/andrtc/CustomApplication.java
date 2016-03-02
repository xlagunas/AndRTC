package xlagunas.cat.andrtc;

import android.app.Application;
import android.content.Context;

import xlagunas.cat.andrtc.di.components.ApplicationComponent;
import xlagunas.cat.andrtc.di.components.DaggerApplicationComponent;
import xlagunas.cat.andrtc.di.modules.ApplicationModule;

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
