package cat.xlagunas.andrtc;

import android.app.Application;
import android.content.Context;

import com.facebook.appevents.AppEventsLogger;

import cat.xlagunas.andrtc.di.components.ApplicationComponent;
import cat.xlagunas.andrtc.di.components.DaggerApplicationComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.di.modules.ApplicationModule;
import cat.xlagunas.andrtc.di.modules.UserModule;
import timber.log.Timber;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 29/02/16.
 */
public class CustomApplication extends Application {

    ApplicationComponent applicationComponent;
    UserComponent userComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setupGraph();
        AppEventsLogger.activateApp(this);

    }

    private void setupGraph() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

    }

    public static CustomApplication getApp(Context context) {
        return (CustomApplication) context.getApplicationContext();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public void createUserComponent(User user) {
        userComponent = applicationComponent.plus(new UserModule(user));
    }

    public UserComponent getUserComponent() {
        if (userComponent == null) {
            getApplicationComponent().getUserCache().getUser().subscribe(this::createUserComponent, e -> createUserComponent(new User()));
        }
        return userComponent;
    }

    public void releaseUserComponent() {
        userComponent = null;
    }

}
