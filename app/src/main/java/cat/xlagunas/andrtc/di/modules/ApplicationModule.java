package cat.xlagunas.andrtc.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import cat.xlagunas.andrtc.ServiceFacade;
import cat.xlagunas.andrtc.UIThread;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.data.cache.UserCacheImpl;
import cat.xlagunas.andrtc.data.repository.FileRepositoryImpl;
import cat.xlagunas.andrtc.data.repository.UserRepositoryImpl;
import cat.xlagunas.andrtc.gcm.RegistrationServiceFacade;
import dagger.Module;
import dagger.Provides;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.FileRepository;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 29/02/16.
 */

@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public Context getAppContext() {
        return application;
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(UserRepositoryImpl userRepositoryImpl) {
        return userRepositoryImpl;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread thread) {
        return thread;
    }

    @Provides
    @Singleton
    UserCache provideUserCache(UserCacheImpl userCache) {
        return userCache;
    }

    @Provides
    @Singleton
    FileRepository provideFileRepository(FileRepositoryImpl fileRepositoryImpl) {
        return fileRepositoryImpl;
    }

    @Provides
    @Singleton
    ServiceFacade provideServerFacade(RegistrationServiceFacade service) {
        return service;
    }
}
