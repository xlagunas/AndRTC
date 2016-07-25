package cat.xlagunas.andrtc.data.net.webrtc;

import cat.xlagunas.andrtc.data.net.webrtc.messages.WebRTCMessage;

/**
 * Created by xlagunas on 25/7/16.
 */
public interface WebRTCCallbacks {
    void onCreateOffer(WebRTCMessage message);
    void onCreateAnswer(WebRTCMessage message);
    void onIceCandidate(WebRTCMessage message);
}
