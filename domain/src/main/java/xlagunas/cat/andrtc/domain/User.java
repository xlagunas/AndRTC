package xlagunas.cat.andrtc.domain;

import java.util.List;

/**
 * Created by xlagunas on 26/02/16.
 */
public class User extends AbstractUser {

    private List<Friend> friends;
    private String password;
    private String facebookId;

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookId() {
        return facebookId;
    }
}
