package xlagunas.cat.andrtc.domain;

/**
 * Created by xlagunas on 26/02/16.
 */
public class Friend extends AbstractUser{

    public final static int ACCEPTED    = 1;
    public final static int REQUESTED   = 2;
    public final static int BLOCKED     = 3;
    public final static int PENDING     = 4;

    private int friendState;

    public Friend(){
        super();
    }

    public int getFriendState() {
        return friendState;
    }

    public void setFriendState(int friendState) {
        this.friendState = friendState;
    }


    @Override
    public String toString() {
        return super.toString() + "Friend{" +
                "friendState=" + friendState +
                '}';
    }
}
