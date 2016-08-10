package cat.xlagunas.andrtc.data.net.webrtc;


import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

/**
 * Created by xlagunas on 3/8/16.
 */

public interface WebRTCManager {

    void init();
    void initLocalSource(SurfaceViewRenderer localRenderer, VideoCapturer capturer);
    void setConferenceListener(WebRTCManagerImpl.ConferenceListener conferenceActivity);
    void addRendererForUser(String userId, SurfaceViewRenderer renderer);
    void stop();

}
