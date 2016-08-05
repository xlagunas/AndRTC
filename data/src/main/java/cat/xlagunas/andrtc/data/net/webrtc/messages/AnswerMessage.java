package cat.xlagunas.andrtc.data.net.webrtc.messages;

import com.google.gson.annotations.SerializedName;

import org.webrtc.SessionDescription;

/**
 * Created by xlagunas on 4/8/16.
 */
public class AnswerMessage extends WebRTCMessage {
    @SerializedName("answer")
    private SessionDescription answer;

    public AnswerMessage(String userId, String roomId, SessionDescription answer) {
        super(userId, roomId);
        this.answer = answer;
    }

    public SessionDescription getAnswer() {
        return answer;
    }
}
