package cat.xlagunas.andrtc.view;

import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 9/03/16.
 */
public interface RegisterDataView {

    void enableSubmitButton();
    void disableSubmitButton();
    void onUserRegistered(User user);
    void showProgress();
}
