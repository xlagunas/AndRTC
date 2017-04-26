package cat.xlagunas.andrtc.view;

import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 25/7/16.
 */
public interface CallRequestDataView {
    void hideAcceptCallButton();

    void startConference(String confId);

    void cancelConference();

    void setOnError(Throwable e);

    void updateUserData(Friend friend);
}
