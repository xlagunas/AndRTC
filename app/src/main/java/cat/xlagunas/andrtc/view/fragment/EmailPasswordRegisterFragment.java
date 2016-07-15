package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.EmailPasswordPresenter;
import cat.xlagunas.andrtc.view.EmailPasswordDataView;
import cat.xlagunas.andrtc.view.util.TextValidator;

/**
 * Created by xlagunas on 14/7/16.
 */
public class EmailPasswordRegisterFragment extends GenericRegisterFragment implements EmailPasswordDataView{

    @Bind(R.id.password)
    TextInputLayout password;
    @Bind(R.id.confirm_password)
    TextInputLayout passwordConfirmation;
    @Bind(R.id.email)
    TextInputLayout email;

    @Inject
    EmailPasswordPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);

        email.getEditText().addTextChangedListener(new TextValidator(email.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateEmail(text);
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

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_email_password_register;
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
    public void setEmail(String email) {
        this.email.getEditText().setText(email);
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
    public void enableNextStep(boolean enable) {
        nextButton.setEnabled(enable);
    }

    private void validateEmail(String text){
        if (presenter.isEmailValid(text)){
            email.setError(null);
            email.setErrorEnabled(false);
        } else {
            setError(email, "Invalid email");
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        this.presenter.destroy();
    }

    @Override
    public void onNextClicked() {
        presenter.onNextRequested();
        super.onNextClicked();
    }
}
