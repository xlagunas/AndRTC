package cat.xlagunas.andrtc.data.net.webrtc;

/**
 * Created by xlagunas on 22/07/16.
 */
public interface Transport {

    String JOIN_ROOM = "call:register";

    void init();
    void connectToRoom(String roomId);
    void disconnect();
    void sendOffer();//TODO PENDING PARAMS)
    void sendAnswer();//TODO PENDING PARAMS)
    void sendIceCandidate();
    void setWebRTCCallbacks(WebRTCCallbacks callbacks);
}
