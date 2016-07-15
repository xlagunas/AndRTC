package cat.xlagunas.andrtc.view.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;

import javax.inject.Inject;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.di.modules.UserModule;
import cat.xlagunas.andrtc.presenter.RegisterPresenter;
import cat.xlagunas.andrtc.view.RegisterDataView;
import cat.xlagunas.andrtc.view.fragment.EmailPasswordRegisterFragment;
import cat.xlagunas.andrtc.view.fragment.GenericRegisterFragment;
import cat.xlagunas.andrtc.view.fragment.UserDetailsRegisterFragment;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 4/04/16.
 */
public class RegisterActivity extends BaseActivity implements HasComponent<UserComponent>, GenericRegisterFragment.OnFragmentChangeRequest, RegisterDataView{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private UserComponent userComponent;

    @Inject
    RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.initializeInjector();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        if (savedInstanceState == null) {
            addFragment(R.id.fragment_container, new EmailPasswordRegisterFragment());
        }
    }

    private void initializeInjector() {
        this.userComponent =
                getApplicationComponent().plus(new UserModule(new User()));

        userComponent.inject(this);
        presenter.setView(this);
    }

    @Override
    public void onNext() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof EmailPasswordRegisterFragment){
            replaceFragment(UserDetailsRegisterFragment.class);
        } else if (fragment instanceof UserDetailsRegisterFragment){
            presenter.registerUser();
        }
    }

    private void replaceFragment(Class newFragment){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(newFragment.getClass().getSimpleName());
        if (fragment == null){
            try {
                fragment = (Fragment) newFragment.newInstance();
            } catch (InstantiationException e) {
                Log.e(TAG, "Error reflection "+ e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Error reflection "+ e);
            }
        }

        replaceFragment(R.id.fragment_container, fragment);
    }

    @Override
    public void onBack() {
        onBackPressed();
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }

    @Override
    public void onUserRegistered(User user) {
        Snackbar.make(findViewById(android.R.id.content), "User created", Snackbar.LENGTH_LONG).show();
    }
}
