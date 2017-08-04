package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.UsernamePasswordDataView;
import cat.xlagunas.andrtc.view.util.FieldValidator;
import cat.xlagunas.andrtc.domain.User;

/**
 * Created by xlagunas on 13/7/16.
 */
public class UsernamePasswordPresenter {
    private final User user;
    private final FieldValidator fieldValidator;

    private UsernamePasswordDataView view;

    private String username;
    private String password;
    private String passwordConfirmation;

    @Inject
    UsernamePasswordPresenter(User user, FieldValidator validator) {
        this.user = user;
        this.fieldValidator = validator;
    }

    public void setView(UsernamePasswordDataView view) {
        this.view = view;
    }

    public boolean isUsernameValid(String username) {
        this.username = username;
        return fieldValidator.isValidUsername(username);
    }

    public boolean isPasswordValid(String password) {
        this.password = password;
        if (fieldValidator.isValidPassword(password)) {
            view.enableConfirmationPassword();
            return true;
        } else {
            view.disableConfirmationPassword();
            return false;
        }
    }

    public boolean validatePasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;

        return password.equals(passwordConfirmation);
    }

    public void onResume() {
        if (user.getUsername() != null && user.getPassword() != null) {
            view.setUsername(user.getUsername());
            view.setPassword(user.getPassword());
            view.setPasswordConfirmation(user.getPassword());
            view.enableNextStep(true);
        } else {
            view.disableConfirmationPassword();
            view.enableNextStep(false);
        }

    }

    public void validateChecks() {
        view.enableNextStep(username != null && isUsernameValid(username) && password != null && isPasswordValid(password)
                && passwordConfirmation != null && validatePasswordConfirmation(passwordConfirmation));

    }

    public void onNextRequested() {
        user.setUsername(username);
        user.setPassword(password);
    }

    public void destroy() {
        view = null;
    }
}
