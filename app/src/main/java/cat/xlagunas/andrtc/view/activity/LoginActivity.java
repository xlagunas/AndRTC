package cat.xlagunas.andrtc.view.activity;

import android.os.Bundle;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.ActivityComponent;
import cat.xlagunas.andrtc.di.modules.ActivityModule;
import cat.xlagunas.andrtc.view.fragment.LoginFragment;
import cat.xlagunas.andrtc.view.fragment.RegisterFragment;

/**
 * Created by xlagunas on 2/03/16.
 */
public class LoginActivity extends BaseActivity implements HasComponent<ActivityComponent>, LoginFragment.FragmentInterface {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this goes in that order because super.onCreate calls the fragment's onCreate
        // before finishing this code
        this.initializeInjector();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        if (savedInstanceState == null) {
            addFragment(R.id.fragment_container, new LoginFragment());
        }
    }

    private void initializeInjector() {
        this.activityComponent =
                getApplicationComponent().plus(new ActivityModule(this));
    }

    @Override
    public ActivityComponent getComponent() {
        return activityComponent;
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
