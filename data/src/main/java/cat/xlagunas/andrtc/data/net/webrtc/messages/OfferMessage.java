package cat.xlagunas.andrtc.data.net.webrtc.messages;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xlagunas on 4/8/16.
 */
public class OfferMessage extends WebRTCMessage {
    @SerializedName("offer")
    private String offer;

    public OfferMessage(String userId, String roomId, String offer) {
        super(userId, roomId);
        this.offer = offer;
    }

    public String getOffer() {
        return offer;
    }
}
