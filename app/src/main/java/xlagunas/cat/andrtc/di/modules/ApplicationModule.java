package xlagunas.cat.andrtc.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xlagunas.cat.andrtc.UIThread;
import xlagunas.cat.andrtc.di.PerActivity;
import xlagunas.cat.data.cache.UserCache;
import xlagunas.cat.data.cache.UserCacheImpl;
import xlagunas.cat.data.repository.UserRepositoryImpl;
import xlagunas.cat.domain.executor.PostExecutionThread;
import xlagunas.cat.domain.interactor.LoginUseCase;
import xlagunas.cat.domain.interactor.UseCase;
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

    @Provides @Singleton
    PostExecutionThread providePostExecutionThread(UIThread thread){
        return thread;
    }

    @Provides @Singleton
    UserCache provideUserCache(UserCacheImpl userCache) {
        return userCache;
    }

}
