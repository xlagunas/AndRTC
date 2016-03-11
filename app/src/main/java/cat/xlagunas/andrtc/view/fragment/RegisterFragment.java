package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.ActivityComponent;
import cat.xlagunas.andrtc.presenter.RegisterPresenter;
import cat.xlagunas.andrtc.view.RegisterDataView;
import cat.xlagunas.andrtc.view.util.TextValidator;
import okhttp3.Credentials;
import cat.xlagunas.andrtc.view.util.FieldValidator;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 9/03/16.
 */
public class RegisterFragment extends BaseFragment implements RegisterDataView {

    @Bind(R.id.username)
    TextInputLayout username;
    @Bind(R.id.name)
    TextInputLayout name;
    @Bind(R.id.surname)
    TextInputLayout surname;
    @Bind(R.id.lastname)
    TextInputLayout lastname;
    @Bind(R.id.password)
    TextInputLayout password;
    @Bind(R.id.email)
    TextInputLayout email;
    @Bind(R.id.submit_register)
    Button registerButton;

    @Inject
    FieldValidator validator;

    @Inject
    RegisterPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(ActivityComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        getEditText(name).addTextChangedListener(new TextValidator(getEditText(name)) {
            @Override
            public void validate(TextView textView, String text) {
                validatePlainText(name, text);
            }
        });
        getEditText(surname).addTextChangedListener(new TextValidator(getEditText(surname)) {
            @Override
            public void validate(TextView textView, String text) {
                validatePlainText(name, text);
            }
        });
        getEditText(lastname).addTextChangedListener(new TextValidator(getEditText(lastname)) {
            @Override
            public void validate(TextView textView, String text) {
                validatePlainText(name, text);
            }
        });
        getEditText(username).addTextChangedListener(new TextValidator(getEditText(username)) {
            @Override
            public void validate(TextView textView, String text) {
                validateUsernameText(username, text);
            }
        });
        getEditText(email).addTextChangedListener(new TextValidator(getEditText(email)) {
            @Override
            public void validate(TextView textView, String text) {
                validateEmailText(email, text);
            }
        });
        getEditText(password).addTextChangedListener(new TextValidator(getEditText(password)) {
            @Override
            public void validate(TextView textView, String text) {
                validatePasswordText(password, text);
            }
        });

        return view;
    }

    private EditText getEditText(TextInputLayout textInputLayout){
        return textInputLayout.getEditText();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
        if (savedInstanceState == null) {
            this.presenter.initialize();
        }
    }

    @OnClick(R.id.submit_register)
    public void register(){
        presenter.registerUser(buildUser());
    }

    private User buildUser(){
        User user = new User();
        user.setUsername(username.getEditText().getText().toString());
        user.setName(name.getEditText().getText().toString());
        user.setSurname(surname.getEditText().getText().toString());
        user.setLastSurname(lastname.getEditText().getText().toString());
        user.setHashedPassword(Credentials.basic(user.getUsername(), password.getEditText().getText().toString()));
        user.setEmail(email.getEditText().toString());

        return user;
    }

    @Override
    public void enableSubmitButton() {
        registerButton.setEnabled(true);
    }

    @Override
    public void disableSubmitButton() {
        registerButton.setEnabled(false);
    }

    @Override
    public void showProgress() {

    }

    private void validatePlainText(TextInputLayout textInputLayout, String text){
        if (validator.isValidTextField(text)){
            textInputLayout.setErrorEnabled(false);
        } else {
            setError(textInputLayout, "This field can't be blank");
        }
    }

    private void validateUsernameText(TextInputLayout textInputLayout, String text){
        if (validator.isValidUsername(text)){
            textInputLayout.setErrorEnabled(false);
        } else {
            setError(textInputLayout, "Invalid Username");
        }
    }

    private void validatePasswordText(TextInputLayout textInputLayout, String text){
        if (validator.isValidPassword(text)){
            textInputLayout.setErrorEnabled(false);
        } else {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Invalid password");
        }
    }

    private void validateEmailText(TextInputLayout textInputLayout, String text){
        if (validator.isValidEmail(text)){
            textInputLayout.setErrorEnabled(false);
        } else {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Email format is incorrect");
        }
    }

    private void setError(TextInputLayout textInputLayout, String message ){
        textInputLayout.setError(message);
        textInputLayout.setErrorEnabled(true);
    }

}
