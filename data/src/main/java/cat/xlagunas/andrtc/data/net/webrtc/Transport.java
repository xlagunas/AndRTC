package cat.xlagunas.andrtc.data.net.webrtc;

/**
 * Created by xlagunas on 22/07/16.
 */
public interface Transport {

    String JOIN_ROOM = "call:register";

    public void init();
    public void connectToRoom(String roomId);
    public void disconnect();
    public void sendOffer();//TODO PENDING PARAMS)
    public void sendAnswer();//TODO PENDING PARAMS)
    public void sendIceCandidate();
}
