package cat.xlagunas.andrtc.data.net.webrtc.messages;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xlagunas on 22/07/16.
 */
public class UserDetailsMessage {
    @SerializedName("idUser")
    private String userId;

    @SerializedName("idCall")
    private String callId;

    public UserDetailsMessage(String userId, String callId) {
        this.userId = userId;
        this.callId = callId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCallId() {
        return callId;
    }
}
