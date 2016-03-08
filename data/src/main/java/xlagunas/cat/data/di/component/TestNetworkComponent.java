package xlagunas.cat.data.di.component;

import javax.inject.Singleton;

import dagger.Component;
import xlagunas.cat.data.di.module.NetworkModule;
import xlagunas.cat.data.mapper.UserEntityMapper;
import xlagunas.cat.data.net.RestApi;

/**
 * Created by xlagunas on 29/02/16.
 */

@Singleton
@Component(modules = NetworkModule.class)
public interface TestNetworkComponent {
    RestApi getRestApi();
    UserEntityMapper getUserEntityMapper();
}
