package cat.xlagunas.andrtc.data.net.webrtc.messages;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xlagunas on 4/8/16.
 */
public class WebRTCMessage {

    @SerializedName("idUser")
    private String userId;

    @SerializedName("idCall")
    private String roomId;

    public WebRTCMessage(String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoomId() {
        return roomId;
    }
}
