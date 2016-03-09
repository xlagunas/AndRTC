package xlagunas.cat.andrtc.view.activity;

import android.os.Bundle;
import android.util.Log;

import xlagunas.cat.andrtc.R;
import xlagunas.cat.andrtc.di.HasComponent;
import xlagunas.cat.andrtc.di.components.DaggerUserComponent;
import xlagunas.cat.andrtc.di.components.UserComponent;
import xlagunas.cat.andrtc.view.fragment.LoginFragment;
import xlagunas.cat.andrtc.view.fragment.RegisterFragment;

/**
 * Created by xlagunas on 2/03/16.
 */
public class LoginActivity extends BaseActivity implements HasComponent<UserComponent>, LoginFragment.FragmentInterface {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private UserComponent userComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        this.initializeInjector();
        if (savedInstanceState == null) {
            addFragment(R.id.fragment_container, new LoginFragment());
        }
    }

    private void initializeInjector() {
        this.userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }

    @Override
    public void onSignInRequested() {
        replaceFragment(R.id.fragment_container, new RegisterFragment());
    }

    @Override
    public void onBackPressed() {
        boolean isLast = !getFragmentManager().popBackStackImmediate();
        if (isLast) {
            super.onBackPressed();
        }
    }
}
