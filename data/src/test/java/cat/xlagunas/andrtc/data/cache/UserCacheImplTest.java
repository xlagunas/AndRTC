package cat.xlagunas.andrtc.data.cache;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.cache.serializer.JsonSerializer;
import cat.xlagunas.andrtc.data.exception.UserNotFoundException;
import cat.xlagunas.andrtc.data.mapper.UserEntityMapper;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by xlagunas on 27/4/17.
 */
@RunWith(JUnit4.class)
public class UserCacheImplTest {

    @Mock
    Context context;
    @Mock
    FileManager fileManager;

    UserEntityMapper userEntityMapper;

    JsonSerializer serializer;

    UserCache userCache;

    @Before
    public void setup() {
        serializer = new JsonSerializer();
        userEntityMapper = new UserEntityMapper();
        MockitoAnnotations.initMocks(this);
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        userCache = new UserCacheImpl(context, fileManager, serializer, userEntityMapper);
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void givenValidUser_whenPutUser_thenUserStored() {

    }

    @Test
    public void givenNoPersistedUser_whenGetUser_thenUserNotFoundException() throws Exception {
        TestSubscriber testSubscriber = new TestSubscriber();
        when(fileManager.getStringFromPreferences(any(), any(), any())).thenReturn(null);

        userCache.getUser().subscribe(testSubscriber);

        testSubscriber.assertError(UserNotFoundException.class);
    }

    @Test
    public void givenPersistedUser_whenGetUser_thenUser() throws Exception {
        TestSubscriber testSubscriber = new TestSubscriber();
        String userPassword = userEntityMapper.encodeBasicAuthPassword("Test", "123456");
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("test");
        user.setFirstSurname("surnameTest");
        user.setLastSurname("lastSurnameTest");
        user.setThumbnail("http://aThumbnail.com");
        user.setUsername("aUsername");
        user.setEmail("test@test.com");
        user.setPassword(userPassword);
        String serializedUser = serializer.serialize(user);

        when(fileManager.getStringFromPreferences(any(), any(), any())).thenReturn(serializedUser);

        userCache.getUser().subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList(userEntityMapper.transformUser(user)));
    }

}