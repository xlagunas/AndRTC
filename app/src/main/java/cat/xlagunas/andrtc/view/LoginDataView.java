package cat.xlagunas.andrtc.view;

import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 11/03/16.
 */
public interface LoginDataView extends LoadDataView{
    void onUserRecovered(User user);
}