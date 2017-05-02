package cat.xlagunas.andrtc.view;

import java.util.List;

import cat.xlagunas.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public interface ListDataView {
    void showProgress();

    void addFriends(List<Friend> friend);

    void showList();
}
