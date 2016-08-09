package cat.xlagunas.andrtc.data.net.webrtc;

import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

/**
 * Created by xlagunas on 3/8/16.
 */

public interface WebRTCManager {

    void init();
    PeerConnectionFactory getPeerConnectionFactory();
    void initLocalSource(SurfaceViewRenderer localRenderer, VideoCapturer capturer);
    void initRemoteSource(SurfaceViewRenderer remoteRenderer);
    void setConferenceListener(WebRTCManagerImpl.ConferenceListener conferenceActivity);
    void stop();

}
