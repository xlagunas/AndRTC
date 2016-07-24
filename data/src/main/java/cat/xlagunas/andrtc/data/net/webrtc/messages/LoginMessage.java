package cat.xlagunas.andrtc.data.net.webrtc.messages;

/**
 * Created by xlagunas on 23/07/16.
 */
public class LoginMessage {

    private final static String TYPE_ANDROID = "ANDROID";
    private String username;
    private String password;
    private String type;

    public LoginMessage(String username, String password) {
        this.username = username;
        this.password = password;
        this.type = TYPE_ANDROID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }
}
