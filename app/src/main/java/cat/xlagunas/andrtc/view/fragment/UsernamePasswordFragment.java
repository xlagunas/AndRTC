package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.UsernamePasswordPresenter;
import cat.xlagunas.andrtc.view.UsernamePasswordDataView;
import cat.xlagunas.andrtc.view.util.TextValidator;

/**
 * Created by xlagunas on 4/04/16.
 */
public class UsernamePasswordFragment extends GenericRegisterFragment implements UsernamePasswordDataView{

    @Bind(R.id.username)
    TextInputLayout username;
    @Bind(R.id.password)
    TextInputLayout password;
    @Bind(R.id.confirm_password)
    TextInputLayout passwordConfirmation;

    @Inject
    UsernamePasswordPresenter presenter;

    @Override
    public int getLayout() {
        return R.layout.fragment_register_username_password;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class).inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);

        username.getEditText().addTextChangedListener(new TextValidator(username.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateUsernameText(text);
                presenter.validateChecks();
            }
        });

        password.getEditText().addTextChangedListener(new TextValidator(password.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
               validatePasswordText(text);
                presenter.validateChecks();
            }
        });

        passwordConfirmation.getEditText().addTextChangedListener(new TextValidator(passwordConfirmation.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validatePasswordConfirmationText(text);
                presenter.validateChecks();
            }
        });

    }

    public void enableNextStep(boolean enable) {
        nextButton.setEnabled(enable);
    }

    @Override
    public void enableConfirmationPassword() {
        passwordConfirmation.setEnabled(true);
    }

    @Override
    public void disableConfirmationPassword() {
        passwordConfirmation.setEnabled(false);
    }

    @Override
    public void setUsername(String username) {
        this.username.getEditText().setText(username);
    }

    @Override
    public void setPassword(String password) {
        this.password.getEditText().setText(password);
    }

    @Override
    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation.getEditText().setText(passwordConfirmation);
    }

    @Override
    public void onNextClicked() {
        presenter.onNextRequested();
        super.onNextClicked();
    }

    private void validateUsernameText(String text){
        if (presenter.isUsernameValid(text)){
            username.setError(null);
            username.setErrorEnabled(false);
        } else {
            setError(username, "Invalid Username");
        }
    }

    private void validatePasswordText(String text){
        if (presenter.isPasswordValid(text)){
            password.setError(null);
            password.setErrorEnabled(false);
        } else {
            password.setErrorEnabled(true);
            setError(password, "Invalid password");
        }
    }

    private void validatePasswordConfirmationText(String text){
        if (presenter.validatePasswordConfirmation(text)){
            passwordConfirmation.setError(null);
            passwordConfirmation.setErrorEnabled(false);
        } else {
            setError(passwordConfirmation, "Passwords doesn't match");
        }
    }

    private void setError(TextInputLayout textInputLayout, String message ){
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(message);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        this.presenter.destroy();
    }
}
