package cat.xlagunas.andrtc.data.net.webrtc.messages;

import com.google.gson.annotations.SerializedName;

import org.webrtc.IceCandidate;

/**
 * Created by xlagunas on 4/8/16.
 */
public class IceCandidateMessage extends WebRTCMessage {
    @SerializedName("candidate")
    private IceCandidate iceCandidate;

    public IceCandidateMessage(String userId, String roomId, IceCandidate iceCandidate) {
        super(userId, roomId);
        this.iceCandidate = iceCandidate;
    }

    public IceCandidate getOffer() {
        return iceCandidate;
    }
}
