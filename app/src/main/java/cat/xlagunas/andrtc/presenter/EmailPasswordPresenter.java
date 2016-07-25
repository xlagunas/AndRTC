package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.EmailPasswordDataView;
import cat.xlagunas.andrtc.view.util.FieldValidator;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 13/7/16.
 */
public class EmailPasswordPresenter {
    private final User user;
    private final FieldValidator fieldValidator;

    private EmailPasswordDataView view;

    private String email;
    private String password;
    private String passwordConfirmation;

    @Inject
    EmailPasswordPresenter(User user, FieldValidator validator){
        this.user = user;
        this.fieldValidator = validator;
    }

    public void setView(EmailPasswordDataView view){
        this.view = view;
    }

    public boolean isEmailValid(String email){
        this.email = email;
        return fieldValidator.isValidEmail(email);
    }

    public boolean isPasswordValid(String password){
        this.password = password;
        if (fieldValidator.isValidPassword(password)){
            view.enableConfirmationPassword();
            return true;
        } else {
            view.disableConfirmationPassword();
            return false;
        }
    }

    public boolean validatePasswordConfirmation(String passwordConfirmation){
        this.passwordConfirmation = passwordConfirmation;

        return password.equals(passwordConfirmation);
    }

    public void onResume() {
        if (user.getUsername() != null && user.getHashedPassword() != null){
            view.setEmail(user.getUsername());
            view.setPassword(user.getHashedPassword());
            view.setPasswordConfirmation(user.getHashedPassword());
            view.enableNextStep(true);
        } else {
            view.disableConfirmationPassword();
            view.enableNextStep(false);
        }

    }

    public void validateChecks(){
        view.enableNextStep(email != null && isEmailValid(email) && password != null && isPasswordValid(password)
                && passwordConfirmation != null && validatePasswordConfirmation(passwordConfirmation));

    }

    public void onNextRequested(){
        user.setEmail(email);
        user.setUsername(email);
        user.setHashedPassword(password);
    }

    public void destroy() {
        view = null;
    }
}
