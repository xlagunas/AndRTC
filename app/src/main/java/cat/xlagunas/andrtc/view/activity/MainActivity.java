package cat.xlagunas.andrtc.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.MainPresenter;
import cat.xlagunas.andrtc.view.fragment.CurrentContactFragment;


/**
 * Created by xlagunas on 9/03/16.
 */
public class MainActivity extends BaseActivity implements HasComponent<UserComponent> {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter presenter;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fab.setVisibility(View.VISIBLE);

        getActivityComponent().inject(this);
        setSupportActionBar(toolbar);
        presenter.initPresenter();

        addFragment(R.id.fragment_container, new CurrentContactFragment());
    }

    @OnClick(R.id.fab)
    public void handleAddFriends(){
        Intent intent = AddContactsActivity.buildAddContactsIntent(MainActivity.this);
        startActivity(intent);
    }

    @Override
    public UserComponent getComponent() {
        return CustomApplication.getApp(this).getUserComponent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout:
                Toast.makeText(MainActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
