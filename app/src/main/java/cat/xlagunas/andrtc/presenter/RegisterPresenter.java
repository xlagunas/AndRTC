package cat.xlagunas.andrtc.presenter;

import android.util.Log;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.RegisterDataView;
import cat.xlagunas.andrtc.view.util.FieldValidator;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.RegisterUseCase;

/**
 * Created by xlagunas on 9/03/16.
 */
public class RegisterPresenter implements Presenter {

    private final static String TAG = RegisterPresenter.class.getSimpleName();

    private final RegisterUseCase registerUseCase;
    private final FieldValidator fieldValidator;
    private final User user;

    private RegisterDataView view;

    @Inject
    public RegisterPresenter(User user, FieldValidator fieldValidator, RegisterUseCase useCase) {
        this.registerUseCase = useCase;
        this.fieldValidator = fieldValidator;
        this.user = user;
    }

    public void setView(RegisterDataView view) {
        this.view = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    public boolean isEmailValid(String email) {
        if (fieldValidator.isValidEmail(email)) {
            user.setEmail(email);
            return true;
        }
        return false;
    }

    public boolean isPasswordValid(String password) {
        if (fieldValidator.isValidPassword(password)) {
            user.setPassword(password);
            return true;
        }
        return false;
    }

    public boolean isFirstnameValid(String firstname) {
        if (fieldValidator.isValidTextField(firstname)) {
            user.setName(firstname);
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastnameValid(String lastName) {
        if (fieldValidator.isValidTextField(lastName)) {
            user.setSurname(lastName);
            return true;
        }
        return false;
    }

    public boolean isUsernameValid(String username) {
        if (fieldValidator.isValidTextField(username)) {
            user.setUsername(username);
            return true;
        }
        return false;
    }

    public void validateData() {
        if (isEmailValid(user.getEmail())
                && isFirstnameValid(user.getName())
                && isLastnameValid(user.getSurname())
                && isPasswordValid(user.getPassword())
                && user.getThumbnail() != null) {
            view.enableRegisterButton();
        } else {
            view.disableRegisterButton();
        }
    }

    public void registerUser() {
        registerUseCase.execute(new DefaultSubscriber<User>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error registering user:", e);
            }

            @Override
            public void onNext(User user) {
                view.onUserRegistered(user);
            }
        });
    }

    @Override
    public void destroy() {
        registerUseCase.unsubscribe();
        this.view = null;

    }

    public void setProfile(String profile) {
        this.user.setThumbnail(profile);
    }
}
