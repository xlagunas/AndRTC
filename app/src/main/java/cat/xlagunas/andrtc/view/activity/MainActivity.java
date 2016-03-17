package cat.xlagunas.andrtc.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.presenter.MainPresenter;


/**
 * Created by xlagunas on 9/03/16.
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter presenter;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getActivityComponent().inject(this);

        presenter.initPresenter();
    }

    @OnClick(R.id.fab)
    public void handleAddFriends(){
        startActivity(new Intent(this, AddContactsActivity.class));
    }
}
