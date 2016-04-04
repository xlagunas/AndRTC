package cat.xlagunas.andrtc.view.activity;

import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cat.xlagunas.andrtc.di.components.ActivityComponent;
import cat.xlagunas.andrtc.di.modules.ActivityModule;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.di.components.ApplicationComponent;

/**
 * Created by xlagunas on 2/03/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment The fragment to be added.
     */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    protected void replaceFragment(int containerViewId, Fragment fragment) {
        replaceFragment(containerViewId, fragment, 0, 0, 0, 0);
    }

    protected void replaceFragment(int containerViewId, Fragment fragment,
                                   @AnimRes int enterAnimation, @AnimRes int exitAnimation,
                                   @AnimRes int enterPopAnimation, @AnimRes int exitPopAnimation){

        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();

        if (enterAnimation != 0 && exitAnimation != 0){
            fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation, enterPopAnimation, exitPopAnimation);
        }

        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.addToBackStack(this.getClass().getSimpleName());
        fragmentTransaction.commit();
    }


    protected ApplicationComponent getApplicationComponent() {
        return CustomApplication.getApp(this).getApplicationComponent();
    }

    protected ActivityComponent getActivityComponent() {
        return getApplicationComponent().plus(new ActivityModule(this));
    }
}