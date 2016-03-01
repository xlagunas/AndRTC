package xlagunas.cat.andrtc.view.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import xlagunas.cat.andrtc.presenter.Presenter;
import xlagunas.cat.andrtc.view.LoadDataView;

public class LoginActivity extends AppCompatActivity implements LoadDataView {

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
    Presenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login)
    public void doLogin(){

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
        return this;
    }
}
