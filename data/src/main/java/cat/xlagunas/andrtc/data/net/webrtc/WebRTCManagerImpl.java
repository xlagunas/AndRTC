package cat.xlagunas.andrtc.data.net.webrtc;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.net.webrtc.messages.WebRTCMessage;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by xlagunas on 25/7/16.
 */
public class WebRTCManagerImpl {

    final Transport transport;

    @Inject
    @Singleton
    public WebRTCManagerImpl(Transport transport){
        this.transport = transport;
    }

    public void start(WebRTCCallbacks callbacks){
        transport.setWebRTCCallbacks(callbacks);
        transport.init();
    }

    public void stop(){
        transport.setWebRTCCallbacks(null);
    }

    public Observable<WebRTCMessage> observable = Observable.create(new Observable.OnSubscribe<WebRTCMessage>() {
        @Override
        public void call(Subscriber<? super WebRTCMessage> subscriber) {

            WebRTCCallbacks callbacks = new WebRTCCallbacks() {
                @Override
                public void onCreateOffer(WebRTCMessage message) {

                }

                @Override
                public void onCreateAnswer(WebRTCMessage message) {
                    subscriber.onNext(null);
                }

                @Override
                public void onIceCandidate(WebRTCMessage message) {

                }
            };

            start(callbacks);

        }


    });

}
