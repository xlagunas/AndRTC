package cat.xlagunas.andrtc.view.util;

import cat.xlagunas.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public class NOOPFriendClickListener implements OnFriendClickListener {
    @Override
    public void onItemClicked(int position, Friend item) {
        //BLANK ON PURPOSE
    }

    @Override
    public void onFriendAccepted(Friend friend) {
        //BLANK ON PURPOSE
    }

    @Override
    public void onFriendRequested(Friend friend) {
        //BLANK ON PURPOSE
    }

    @Override
    public void onFriendRejected(Friend friend) {
        //BLANK ON PURPOSE
    }
}
