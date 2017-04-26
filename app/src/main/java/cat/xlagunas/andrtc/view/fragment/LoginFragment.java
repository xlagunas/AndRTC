package cat.xlagunas.andrtc.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.ActivityComponent;
import cat.xlagunas.andrtc.presenter.LoginPresenter;
import cat.xlagunas.andrtc.view.LoginDataView;
import xlagunas.cat.andrtc.domain.User;

public class LoginFragment extends BaseFragment implements LoginDataView {

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.login)
    Button loginButton;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.root_layout)
    LinearLayout linearLayout;

    @BindView(R.id.sign_in)
    TextView signIn;

    @BindView(R.id.google_login_button)
    SignInButton googleLoginButton;

    @Inject
    LoginPresenter loginPresenter;

    FragmentInterface fragmentInterface;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(ActivityComponent.class).inject(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            fragmentInterface = (FragmentInterface) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity must implement FragmentInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (fragmentInterface != null) {
            fragmentInterface = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.loginPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.loginPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.loginPresenter.destroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginPresenter.setView(this);
        if (savedInstanceState == null) {
            this.loginPresenter.initialize();
        }
    }

    @OnClick(R.id.login)
    public void doLogin() {
        loginPresenter.doLogin(username.getText().toString(), password.getText().toString());
    }

    @OnClick(R.id.sign_in)
    public void signIn() {
        fragmentInterface.onSignInRequested();
    }

    @Override
    public void showLoading() {
        linearLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRetry() {
    }

    @Override
    public void hideRetry() {
    }

    @Override
    public void showError(String message) {
        Toast.makeText(context(), "Error login in", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context context() {
        return this.getActivity().getApplicationContext();
    }

    @Override
    public void onUserRecovered(User user) {
        CustomApplication.getApp(getActivity()).createUserComponent(user);
        fragmentInterface.onSuccessfullyLogged();
    }

    @OnClick(R.id.google_login_button)
    void onGoogleLoginClick() {
        loginPresenter.doGoogleLogin();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface FragmentInterface {
        void onSignInRequested();

        void onSuccessfullyLogged();
    }
}
