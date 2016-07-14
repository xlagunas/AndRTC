package cat.xlagunas.andrtc.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.ActivityComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.di.modules.ActivityModule;
import cat.xlagunas.andrtc.di.modules.UserModule;
import cat.xlagunas.andrtc.view.fragment.EmailPasswordRegisterFragment;
import cat.xlagunas.andrtc.view.fragment.GenericRegisterFragment;
import cat.xlagunas.andrtc.view.fragment.ImagePickerFragment;
import cat.xlagunas.andrtc.view.fragment.UserDetailsFragment;
import cat.xlagunas.andrtc.view.fragment.UserDetailsRegisterFragment;
import cat.xlagunas.andrtc.view.fragment.UsernamePasswordFragment;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 4/04/16.
 */
public class RegisterActivity extends BaseActivity implements HasComponent<UserComponent>, GenericRegisterFragment.OnFragmentChangeRequest{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private User user;
    private UserComponent userComponent;

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
        user = new User();
        this.userComponent =
                getApplicationComponent().plus(new UserModule(user));
    }

    @Override
    public void onNext() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        if (fragment instanceof  UsernamePasswordFragment){
//            replaceFragment(UserDetailsFragment.class);
//        } else if (fragment instanceof UserDetailsFragment){
//            replaceFragment(ImagePickerFragment.class);
//        }
        if (fragment instanceof EmailPasswordRegisterFragment){
            replaceFragment(UserDetailsRegisterFragment.class);
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

//        replaceFragment(R.id.fragment_container,
//                fragment, R.anim.slide_in_right, R.anim.slide_in_left,
//                R.anim.slide_out_left, R.anim.slide_out_right);
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
}
