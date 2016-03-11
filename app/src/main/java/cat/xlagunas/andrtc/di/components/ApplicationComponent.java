package cat.xlagunas.andrtc.di.components;

import javax.inject.Singleton;

import cat.xlagunas.andrtc.view.activity.BaseActivity;
import dagger.Component;
import cat.xlagunas.andrtc.di.modules.ApplicationModule;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.data.di.module.NetworkModule;
import cat.xlagunas.andrtc.data.net.RestApi;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 29/02/16.
 */

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {
    public void inject(BaseActivity activity);

    RestApi restApi();
    PostExecutionThread postExecutionThread();
    UserRepository userRepository();
    UserCache userCache();
}
