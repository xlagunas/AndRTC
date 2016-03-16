package cat.xlagunas.andrtc.view;

import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 15/03/16.
 */
public interface SearchListView {

    void doSearch(String filter);
    void showEmpty();
    void showList();
    void addFriendToList(Friend friend);
    void clearAdapter();
}
