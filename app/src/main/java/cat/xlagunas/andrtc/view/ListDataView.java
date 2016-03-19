package cat.xlagunas.andrtc.view;

import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public interface ListDataView {
    void showProgress();
    void addFriend(Friend friend);
    void showList();
}
