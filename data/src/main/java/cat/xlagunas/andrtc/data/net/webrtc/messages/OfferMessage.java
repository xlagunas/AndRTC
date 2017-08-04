package cat.xlagunas.andrtc.data.net.webrtc.messages;

import com.google.gson.annotations.SerializedName;

import org.webrtc.SessionDescription;

/**
 * Created by xlagunas on 4/8/16.
 */
public class OfferMessage extends WebRTCMessage {
    @SerializedName("offer")
    private SessionDescription offer;

    public OfferMessage(String userId, String roomId, SessionDescription offer) {
        super(userId, roomId);
        this.offer = offer;
    }

    public SessionDescription getOffer() {
        return offer;
    }
}
