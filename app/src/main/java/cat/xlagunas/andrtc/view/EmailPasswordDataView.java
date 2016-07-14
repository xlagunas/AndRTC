package cat.xlagunas.andrtc.view;

/**
 * Created by xlagunas on 13/7/16.
 */
public interface EmailPasswordDataView extends BaseRegisterDataView {

    void enableConfirmationPassword();
    void disableConfirmationPassword();

    void setEmail(String email);
    void setPassword(String password);
    void setPasswordConfirmation(String passwordConfirmation);
}
