package cat.xlagunas.andrtc.data.net.webrtc;

import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;

/**
 * Created by xlagunas on 3/8/16.
 */

public interface WebRTCManager {

    void init();
    PeerConnectionFactory getPeerConnectionFactory();
    VideoSource initLocalSource(SurfaceViewRenderer localRenderer, VideoCapturer capturer);
    void stop();
}
