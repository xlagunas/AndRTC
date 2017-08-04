package cat.xlagunas.andrtc.data.net.params;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xlagunas on 11/03/16.
 */
public class TokenParams {
    @SerializedName("token")
    private String token;

    public TokenParams(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
