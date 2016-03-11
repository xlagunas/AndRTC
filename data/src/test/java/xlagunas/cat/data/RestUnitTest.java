package xlagunas.cat.data;

import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import cat.xlagunas.andrtc.data.UserEntity;
import okhttp3.Credentials;
import okio.ByteString;
import rx.Subscriber;
import cat.xlagunas.andrtc.data.di.component.DaggerTestNetworkComponent;
import cat.xlagunas.andrtc.data.di.component.TestNetworkComponent;
import cat.xlagunas.andrtc.data.di.module.NetworkModule;
import cat.xlagunas.andrtc.data.net.RestApi;
import cat.xlagunas.andrtc.data.net.params.LoginParams;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RestUnitTest {
    private RestApi restApi;

    @Before public void setUp() {
    TestNetworkComponent module = DaggerTestNetworkComponent.builder().networkModule(new NetworkModule()).build();
    restApi = module.getRestApi();
    }
    @Test
    public void test_login() throws Exception {
        restApi.loginUser(new LoginParams("xlagunas", "123456"))
               .subscribe(new Subscriber<UserEntity>() {
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
                   public void onNext(UserEntity user) {
                       System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(user, UserEntity.class).toString());
                   }
               });
    }

    @Test
    public void test_credentials() throws Exception {
        String username = "aUser";
        String password = "aPass";
        String creds = Credentials.basic(username, password);

        String str = ByteString.decodeBase64(creds.substring(6)).utf8();
        assert(password.equals(str.substring(username.length()+1)));
    }
}