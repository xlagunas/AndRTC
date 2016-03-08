package xlagunas.cat.andrtc.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xlagunas.cat.andrtc.R;
import xlagunas.cat.andrtc.di.components.UserComponent;
import xlagunas.cat.andrtc.presenter.LoginPresenter;
import xlagunas.cat.andrtc.view.LoadDataView;

public class LoginFragment extends BaseFragment implements LoadDataView {

    @Bind(R.id.username)
    EditText username;

    @Bind(R.id.password)
    EditText password;

    @Bind(R.id.login)
    Button loginButton;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.root_layout)
    LinearLayout linearLayout;

    @Inject
    LoginPresenter loginPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(UserComponent.class).inject(this);
    }

    @Override public void onResume() {
        super.onResume();
        this.loginPresenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        this.loginPresenter.pause();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public void onDestroy() {
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
    public void doLogin(){
        loginPresenter.doLogin(username.getText().toString(), password.getText().toString());
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
    public void showRetry() {}

    @Override
    public void hideRetry() {}

    @Override
    public void showError(String message) {
        Toast.makeText(context(), "Error login in", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context context() {
        return this.getActivity().getApplicationContext();
    }
}
