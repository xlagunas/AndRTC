package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.EmailPasswordDataView;
import cat.xlagunas.andrtc.view.util.FieldValidator;
import cat.xlagunas.andrtc.domain.User;

/**
 * Created by xlagunas on 13/7/16.
 */
public class EmailPasswordPresenter {
    private final User user;
    private final FieldValidator fieldValidator;

    private EmailPasswordDataView view;

    private String email;
    private String password;

    @Inject
    EmailPasswordPresenter(User user, FieldValidator validator) {
        this.user = user;
        this.fieldValidator = validator;
    }

    public void setView(EmailPasswordDataView view) {
        this.view = view;
    }

    public boolean isEmailValid(String email) {
        this.email = email;
        return fieldValidator.isValidEmail(email);
    }

    public boolean isPasswordValid(String password) {
        this.password = password;
        return fieldValidator.isValidPassword(password);
    }

    public void onResume() {
        if (user.getUsername() != null && user.getPassword() != null) {
            view.setEmail(user.getUsername());
            view.setPassword(user.getPassword());
            view.enableNextStep(true);
        } else {
            view.enableNextStep(false);
        }

    }

    public void validateChecks() {
        view.enableNextStep(email != null && isEmailValid(email) && password != null && isPasswordValid(password));
    }

    public void onNextRequested() {
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(password);
    }

    public void destroy() {
        view = null;
    }
}
