package xlagunas.cat.andrtc.domain;

import java.util.List;

/**
 * Created by xlagunas on 26/02/16.
 */
public class User extends AbstractUser {

    private List<Friend> friends;
    private String hashedPassword;

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
