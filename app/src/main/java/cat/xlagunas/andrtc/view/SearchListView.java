package cat.xlagunas.andrtc.view;

import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 15/03/16.
 */
public interface SearchListView {

    public void doSearch(String filter);
    public void showEmpty();
    public void showList();
    public void addFriendToList(Friend friend);
}
