package xlagunas.cat.data;

import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import rx.Subscriber;
import xlagunas.cat.data.di.component.DaggerTestNetworkComponent;
import xlagunas.cat.data.di.component.TestNetworkComponent;
import xlagunas.cat.data.di.module.NetworkModule;
import xlagunas.cat.domain.User;
import xlagunas.cat.domain.repository.UserRepository;

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
                       System.out.println("Entra al onError");
                       e.printStackTrace();
                   }

                   @Override
                   public void onNext(User user) {
                       System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(user, User.class).toString());
                   }
               });

    }
}