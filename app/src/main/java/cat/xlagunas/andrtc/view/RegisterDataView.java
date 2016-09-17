package cat.xlagunas.andrtc.view;

import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 9/03/16.
 */
public interface RegisterDataView {

    void enableRegisterButton();

    void disableRegisterButton();

    void onUserRegistered(User user);

}
