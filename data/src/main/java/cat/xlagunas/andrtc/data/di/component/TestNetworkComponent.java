package cat.xlagunas.andrtc.data.di.component;

import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.di.module.NetworkModule;
import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import cat.xlagunas.andrtc.data.net.RestApi;
import dagger.Component;

/**
 * Created by xlagunas on 29/02/16.
 */

@Singleton
@Component(modules = NetworkModule.class)
public interface TestNetworkComponent {
    RestApi getRestApi();
    UserEntityMapper getUserEntityMapper();
}
