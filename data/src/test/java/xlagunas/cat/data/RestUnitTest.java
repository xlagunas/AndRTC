package xlagunas.cat.data;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.di.component.DaggerTestNetworkComponent;
import cat.xlagunas.andrtc.data.di.component.TestNetworkComponent;
import cat.xlagunas.andrtc.data.di.module.NetworkModule;
import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import cat.xlagunas.andrtc.data.net.RestApi;
import cat.xlagunas.andrtc.data.net.params.LoginParams;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.ByteString;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import xlagunas.cat.andrtc.domain.Friend;

import static org.mockito.Matchers.any;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RestUnitTest {
    private RestApi restApi;
    private UserEntityMapper mapper;

    @Before
    public void setUp() {
        TestNetworkComponent module = DaggerTestNetworkComponent.builder().networkModule(new NetworkModule()).build();
        restApi = module.getRestApi();

        mapper = new UserEntityMapper();
    }

    @Test
    public void givenValidUser_whenLogin_thenUserEntity() throws Exception {
        TestSubscriber<UserEntity> testSubscriber = new TestSubscriber<>();

        restApi.loginUser(new LoginParams("xlagunas", "123456"))
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(any());
        testSubscriber.assertNoErrors();
    }

    @Test
    public void test_credentials() throws Exception {
        String username = "aUser";
        String password = "aPass";
        String creds = Credentials.basic(username, password);

        String str = ByteString.decodeBase64(creds.substring(6)).utf8();
        assert (password.equals(str.substring(username.length() + 1)));
    }

    @Test
    public void test_search_users() throws Exception {
        restApi.findUsers(Credentials.basic("xlagunas", "123456"), "lag")
                .flatMapIterable(friendEntities -> friendEntities)
                .flatMap(friendEntity -> Observable.just(mapper.mapFriendEntity(friendEntity)))
                .subscribe(new Subscriber<Friend>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Friend user) {
                        System.out.println(user.toString());
                    }
                });
    }

    @Test
    public void test_image_post() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("test.png");
        File f = new File(resource.getPath());

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), f);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("thumbnail", resource.getPath(), requestFile);

        restApi.putUserProfilePicture(Credentials.basic("xlagunas", "123456"), body)
                .subscribe(new Subscriber<UserEntity>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(UserEntity entity) {
                        System.out.println("on Next");
                        System.out.println(entity.toString());
                    }
                });
    }
}