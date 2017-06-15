package cat.xlagunas.andrtc.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.FileRepository;
import cat.xlagunas.andrtc.domain.repository.UserRepository;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.when;

/**
 * Created by xlagunas on 18/4/17.
 */

@RunWith(JUnit4.class)
public class RegisterGCMTokenUseCaseTest {

    private static final String TOKEN_VALUE = "tokenValue";

    @Mock
    PostExecutionThread postExecutionThread;

    @Mock
    User user;

    @Mock
    UserRepository userRepository;

    @Mock
    FileRepository fileRepository;

    RegisterGCMTokenUseCase registerUseCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        registerUseCase = new RegisterGCMTokenUseCase(postExecutionThread, user, userRepository, fileRepository);
    }

    @Test
    public void whenToken_thenRequestMade() throws Exception {
        when(fileRepository.getStoredToken()).thenReturn(Observable.just(TOKEN_VALUE));
        when(userRepository.registerGCMToken(user, TOKEN_VALUE)).thenReturn(Observable.just(true));
        when(postExecutionThread.getScheduler()).thenReturn(Schedulers.immediate());

        TestSubscriber<Boolean> subscriber = new TestSubscriber();

        registerUseCase.execute(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertReceivedOnNext(Arrays.asList(true));
    }

    @Test
    public void whenNoToken_thenRequestMade() throws Exception {
        when(fileRepository.getStoredToken()).thenReturn(Observable.empty());
        when(postExecutionThread.getScheduler()).thenReturn(Schedulers.immediate());

        TestSubscriber<Boolean> subscriber = new TestSubscriber();

        registerUseCase.execute(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertReceivedOnNext(Collections.emptyList());
    }

}