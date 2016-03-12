package cat.xlagunas.andrtc.view.activity;

import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.presenter.MainPresenter;


/**
 * Created by xlagunas on 9/03/16.
 */
public class MainActivity extends BaseActivity {


    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        presenter.initPresenter();
    }
}
