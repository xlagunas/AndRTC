package cat.xlagunas.andrtc.di.modules;

import org.webrtc.EglBase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cat.xlagunas.andrtc.data.net.webrtc.SocketIOTransport;
import cat.xlagunas.andrtc.data.net.webrtc.Transport;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManager;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManagerImpl;
import cat.xlagunas.andrtc.di.ConferenceScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by xlagunas on 4/8/16.
 */

@Module
public class ConferenceModule {
    private final EglBase rootEglContext;
    private final String roomId;

    public ConferenceModule(EglBase rootContext, String roomId){
        this.rootEglContext = rootContext;
        this.roomId = roomId;
    }

    @Provides
    @ConferenceScope
    public EglBase getEglContext(){
        return rootEglContext;
    }

    @Provides
    @ConferenceScope
    public String getRoomId(){
        return roomId;
    }

    @Provides
    @ConferenceScope
    public Transport getTransport(SocketIOTransport transport){
        return transport;
    }

    @Provides
    @ConferenceScope
    public WebRTCManager getManager(WebRTCManagerImpl webRTCManager){
        return webRTCManager;
    }

    @Provides
    @ConferenceScope
    public Executor getExecutor(){
        return Executors.newSingleThreadExecutor();
    }


}
