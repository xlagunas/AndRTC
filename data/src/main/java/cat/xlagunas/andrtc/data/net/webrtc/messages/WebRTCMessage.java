package cat.xlagunas.andrtc.data.net.webrtc.messages;

/**
 * Created by xlagunas on 25/7/16.
 */
public interface WebRTCMessage {
    int LOGIN = 1000;
    int CREATE_OFFER = 1001;
    int CREATE_ANSWER = 1002;
    int ICE_CANDIDATE = 1003;

    int getType();
    String getMessage();
}
