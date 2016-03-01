package xlagunas.cat.data.di.component;

import dagger.Component;
import xlagunas.cat.data.di.module.NetworkModule;
import xlagunas.cat.data.mapper.UserEntityMapper;
import xlagunas.cat.data.net.RestApi;
import xlagunas.cat.domain.repository.UserRepository;

/**
 * Created by xlagunas on 29/02/16.
 */

@Component(modules = NetworkModule.class)
public interface TestNetworkComponent {
    RestApi getRestApi();
    UserEntityMapper getUserEntityMapper();
    UserRepository getUserRepository();
}
