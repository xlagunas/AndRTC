package cat.xlagunas.andrtc.view;

/**
 * Created by xlagunas on 13/7/16.
 */
public interface UsernamePasswordDataView extends BaseRegisterDataView {

    void enableConfirmationPassword();
    void disableConfirmationPassword();

    void setUsername(String username);
    void setPassword(String password);
    void setPasswordConfirmation(String passwordConfirmation);
}
