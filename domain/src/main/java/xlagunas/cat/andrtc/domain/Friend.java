package xlagunas.cat.andrtc.domain;

/**
 * Created by xlagunas on 26/02/16.
 */
public class Friend extends AbstractUser implements Comparable<Friend>{

    public final static int CREATED     = 0;
    public final static int ACCEPTED    = 1;
    public final static int REQUESTED   = 2;
    public final static int BLOCKED     = 3;
    public final static int PENDING     = 4;

    public final static String ACCEPTED_RELATIONSHIP    = "accepted";
    public final static String REJECTED_RELATIONSHIP    = "rejected";
    public final static String PENDING_RELATIONSHIP     = "pending";


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

    @Override
    public int compareTo(Friend friend) {
        if (this.getFriendState() == friend.getFriendState()){
            return this.getUsername().compareTo(friend.getUsername());
        }

        return friend.getFriendState() - this.getFriendState();
    }

    public boolean contactMatchesSearchKeywords(String keyword){
        StringBuilder fullname = new StringBuilder();
        fullname.append(this.getName());

        if (this.getSurname() != null) {
            fullname.append(" ");
            fullname.append(this.getSurname());
        }
        if (this.getLastSurname() != null){
            fullname.append(" ");
            fullname.append(this.getLastSurname());
        }
        return this.getUsername().contains(keyword) || fullname.toString().contains(keyword);
    }

}
