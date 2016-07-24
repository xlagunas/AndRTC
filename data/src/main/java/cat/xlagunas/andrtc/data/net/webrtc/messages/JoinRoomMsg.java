package cat.xlagunas.andrtc.data.net.webrtc.messages;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xlagunas on 22/07/16.
 */
public class JoinRoomMsg {
    @SerializedName("id")
    private String roomId;

    public JoinRoomMsg(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
