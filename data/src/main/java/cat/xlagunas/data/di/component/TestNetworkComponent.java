package cat.xlagunas.data.di.component;

import javax.inject.Singleton;

import cat.xlagunas.data.di.module.NetworkModule;
import dagger.Component;
import cat.xlagunas.data.mapper.UserEntityMapper;
import cat.xlagunas.data.net.RestApi;

/**
 * Created by xlagunas on 29/02/16.
 */

@Singleton
@Component(modules = NetworkModule.class)
public interface TestNetworkComponent {
    RestApi getRestApi();
    UserEntityMapper getUserEntityMapper();
}
