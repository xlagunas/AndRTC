package xlagunas.cat.andrtc.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.runners.JUnit44RunnerImpl;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.FileRepository;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

import static org.mockito.Mockito.verify;
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