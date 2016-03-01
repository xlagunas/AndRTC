package xlagunas.cat.andrtc.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xlagunas.cat.data.repository.UserRepositoryImpl;
import xlagunas.cat.domain.repository.UserRepository;

/**
 * Created by xlagunas on 29/02/16.
 */

@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application app){
        this.application = app;
    }

    @Provides
    @Singleton
    public Context getAppContext(){
        return application;
    }

    @Provides @Singleton
    UserRepository provideUserRepository(UserRepositoryImpl userRepositoryImpl){
        return userRepositoryImpl;
    }


}
