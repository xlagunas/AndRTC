package xlagunas.cat.data;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import rx.Subscriber;
import rx.schedulers.Schedulers;
import xlagunas.cat.data.di.component.DaggerTestNetworkComponent;
import xlagunas.cat.data.di.component.TestNetworkComponent;
import xlagunas.cat.data.di.module.NetworkModule;
import xlagunas.cat.data.net.RestApi;
import xlagunas.cat.data.net.params.LoginParams;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RestUnitTest {

    @Inject
    private RestApi restApi;

    @Before public void setUp() {
        TestNetworkComponent module = DaggerTestNetworkComponent.builder().networkModule(new NetworkModule()).build();
        restApi = module.getRestApi();

    }
    @Test
    public void test_login() throws Exception {
        restApi.loginUser(new LoginParams("xlagunas", "123456"))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.immediate())
                .subscribe(new Subscriber<UserEntity>() {
            @Override
            public void onCompleted() {
                System.out.println("On completed");
            }

            @Override
            public void onError(Throwable e) {
                System.err.println(e);
            }

            @Override
            public void onNext(UserEntity userEntity) {
                System.out.println(new Gson().toJson(userEntity, UserEntity.class).toString());
            }
        });
    }
}