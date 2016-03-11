package cat.xlagunas.andrtc.data.net.params;

/**
 * Created by xlagunas on 11/03/16.
 */
public class TokenParams {
    private String token;
    private long expirationDate;

    public String getToken() {
        return token;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public TokenParams(String token, long expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }
}
