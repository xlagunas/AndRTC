package cat.xlagunas.andrtc.data.net.webrtc;

import org.webrtc.SessionDescription;

/**
 * Created by xlagunas on 22/07/16.
 */
public interface Transport {

    void init();
    void disconnect();
    void sendOffer(String userId, SessionDescription localDescription);
    void sendAnswer(String userId, SessionDescription localDescription);
    void sendIceCandidate();
    void setWebRTCCallbacks(WebRTCCallbacks callbacks);
}
