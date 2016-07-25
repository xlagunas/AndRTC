package xlagunas.cat.data;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cat.xlagunas.andrtc.data.net.webrtc.SocketIOTransport;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManager;
import cat.xlagunas.andrtc.data.net.webrtc.messages.WebRTCMessage;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 22/07/16.
 */
public class SocketIOTest {

    private User user;
    private CountDownLatch lock = new CountDownLatch(1);


    @Before
    public void init(){
        user = new User();
        user.setUsername("xlagunas");
        user.setPassword("123456");
    }

    @Test
    public void checkConnection(){
        WebRTCManager manager = new WebRTCManager(new SocketIOTransport(user));
        manager.observable.subscribeOn(Schedulers.immediate()).observeOn(Schedulers.immediate())
                .subscribe(new Subscriber<WebRTCMessage>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onNext(WebRTCMessage webRTCMessage) {
                        System.out.println("next");
                        lock.countDown();
                    }
                });

        try {
            lock.await(15000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
