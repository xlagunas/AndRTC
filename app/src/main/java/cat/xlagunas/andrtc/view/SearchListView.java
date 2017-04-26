package cat.xlagunas.andrtc.view;

import java.util.List;

import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 15/03/16.
 */
public interface SearchListView {

    void doSearch(String filter);

    void showEmpty();

    void showList();

    void addFriends(List<Friend> friend);

    void clearAdapter();

    void showConfirmationError(Throwable e);

    void showConfirmation();

    void notifyContactUpdate(Friend friend, String message);

    void notifiyUpdateError(Friend friend, Throwable e);
}
