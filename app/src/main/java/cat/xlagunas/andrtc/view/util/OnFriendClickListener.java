package cat.xlagunas.andrtc.view.util;

import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 17/03/16.
 */
public interface OnFriendClickListener {
    void onItemClicked(int position, Friend item);

    void onFriendAccepted(Friend friend);

    void onFriendRequested(Friend friend);

    void onFriendRejected(Friend friend);
}
