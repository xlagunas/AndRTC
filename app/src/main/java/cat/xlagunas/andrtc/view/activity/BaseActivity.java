package cat.xlagunas.andrtc.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    protected void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
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