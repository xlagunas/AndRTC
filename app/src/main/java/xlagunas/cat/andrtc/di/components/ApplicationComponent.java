package xlagunas.cat.andrtc.di.components;

import javax.inject.Singleton;

import dagger.Component;
import xlagunas.cat.andrtc.di.modules.ApplicationModule;
import xlagunas.cat.andrtc.view.activity.BaseActivity;
import xlagunas.cat.data.di.module.NetworkModule;
import xlagunas.cat.data.net.RestApi;
import xlagunas.cat.domain.executor.PostExecutionThread;
import xlagunas.cat.domain.repository.UserRepository;

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
}
