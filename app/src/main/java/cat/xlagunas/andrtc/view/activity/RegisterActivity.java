package cat.xlagunas.andrtc.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.di.modules.UserModule;
import cat.xlagunas.andrtc.presenter.RegisterPresenter;
import cat.xlagunas.andrtc.view.RegisterDataView;
import cat.xlagunas.andrtc.view.fragment.VivProfileImageFragment;
import cat.xlagunas.andrtc.view.util.TextValidator;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 4/04/16.
 */
public class RegisterActivity extends BaseActivity implements HasComponent<UserComponent>, RegisterDataView {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    public static final int RESULT_CODE = 123;

    private UserComponent userComponent;

    @BindView(R.id.first_name)
    TextInputLayout firstname;

    @BindView(R.id.last_name)
    TextInputLayout lastname;

    @BindView(R.id.email)
    TextInputLayout email;

    @BindView(R.id.username)
    TextInputLayout username;

    @BindView(R.id.password)
    TextInputLayout password;

    @BindView(R.id.submit_register)
    ImageView registerButton;

    @Inject
    RegisterPresenter presenter;

    @OnClick(R.id.submit_register)
    public void onRegister() {
        presenter.registerUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.initializeInjector();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        ButterKnife.bind(this);
        bindTextWatchers();
        addProfileImageFragment();
    }

    private void addProfileImageFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.camera_button_placeholder, VivProfileImageFragment.makeInstance())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((VivProfileImageFragment) getSupportFragmentManager().findFragmentById(R.id.camera_button_placeholder))
                .setImageSelectedListener(this::getImage);
    }

    private void bindTextWatchers() {
        getEditTextFromTextInputLayout(firstname).addTextChangedListener(new TextValidator(getEditTextFromTextInputLayout(firstname)) {
            @Override
            public void validate(TextView textView, String text) {
                if (presenter.isFirstnameValid(text)) {
                    firstname.setError(null);
                    firstname.setErrorEnabled(false);
                } else {
                    firstname.setError("Invalid firstname");
                }
                presenter.validateData();
            }
        });

        getEditTextFromTextInputLayout(lastname).addTextChangedListener(new TextValidator(getEditTextFromTextInputLayout(lastname)) {
            @Override
            public void validate(TextView textView, String text) {
                if (presenter.isLastnameValid(text)) {
                    lastname.setError(null);
                    lastname.setErrorEnabled(false);
                } else {
                    lastname.setError("Invalid lastname");
                }
                presenter.validateData();

            }
        });
        getEditTextFromTextInputLayout(email).addTextChangedListener(new TextValidator(getEditTextFromTextInputLayout(email)) {
            @Override
            public void validate(TextView textView, String text) {
                if (presenter.isEmailValid(text)) {
                    email.setError(null);
                    email.setErrorEnabled(false);
                } else {
                    email.setError("Invalid email address");
                }
                presenter.validateData();
            }
        });
        getEditTextFromTextInputLayout(username).addTextChangedListener(new TextValidator(getEditTextFromTextInputLayout(username)) {
            @Override
            public void validate(TextView textView, String text) {
                if (presenter.isUsernameValid(text)) {
                    username.setError(null);
                    username.setErrorEnabled(false);
                } else {
                    username.setError("Invalid username");
                }
                presenter.validateData();
            }
        });
        getEditTextFromTextInputLayout(password).addTextChangedListener(new TextValidator(getEditTextFromTextInputLayout(password)) {
            @Override
            public void validate(TextView textView, String text) {
                if (presenter.isPasswordValid(text)) {
                    password.setError(null);
                    password.setErrorEnabled(false);
                } else {
                    password.setError("Invalid password");
                }
                presenter.validateData();
            }
        });
    }

    private void initializeInjector() {
        this.userComponent =
                getApplicationComponent().plus(new UserModule(new User()));

        userComponent.inject(this);
        presenter.setView(this);
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }

    @Override
    public void enableRegisterButton() {
        registerButton.setEnabled(true);
    }

    @Override
    public void disableRegisterButton() {
        registerButton.setEnabled(false);
    }

    @Override
    public void onUserRegistered(User user) {
        Snackbar.make(findViewById(android.R.id.content), "User created", Snackbar.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    private EditText getEditTextFromTextInputLayout(TextInputLayout layout) {
        return layout.getEditText();
    }

    private void getImage(Uri uri) {
        Log.d(TAG, "Uri: " + uri.getPath());
        presenter.setProfile(uri.getPath());
        presenter.validateData();
    }

}
