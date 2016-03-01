package xlagunas.cat.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xlagunas.cat.data.di.component.DaggerTestNetworkComponent;
import xlagunas.cat.data.di.component.TestNetworkComponent;
import xlagunas.cat.data.di.module.NetworkModule;
import xlagunas.cat.data.mapper.UserEntityMapper;
import xlagunas.cat.data.net.RestApi;
import xlagunas.cat.data.net.params.LoginParams;
import xlagunas.cat.domain.User;
import xlagunas.cat.domain.repository.UserRepository;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RestUnitTest {


    @Inject
    UserRepository userRepository;


    @Before public void setUp() {
        TestNetworkComponent module = DaggerTestNetworkComponent.builder().networkModule(new NetworkModule()).build();
        userRepository = module.getUserRepository();

    }
    @Test
    public void test_login() throws Exception {
        userRepository.login("xlagunas", "123456")
               .subscribe(new Subscriber<User>() {
                   @Override
                   public void onCompleted() {
                       System.out.println("On completed");
                   }

                   @Override
                   public void onError(Throwable e) {
                       e.printStackTrace();
                   }

                   @Override
                   public void onNext(User user) {
                       System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(user, User.class).toString());
                   }
               });

    }
}