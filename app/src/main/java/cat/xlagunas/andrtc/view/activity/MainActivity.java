package cat.xlagunas.andrtc.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.ServiceFacade;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.MainPresenter;
import cat.xlagunas.andrtc.view.LogOutDataView;
import cat.xlagunas.andrtc.view.fragment.CurrentContactFragment;

/**
 * Created by xlagunas on 9/03/16.
 */
public class MainActivity extends BaseActivity implements HasComponent<UserComponent>, LogOutDataView {

    @Inject
    MainPresenter presenter;

    @Inject
    ServiceFacade serviceFacade;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        fab.setVisibility(View.VISIBLE);

        getActivityComponent().inject(this);
        setSupportActionBar(toolbar);
        serviceFacade.startService();
        presenter.setView(this);

        if (savedInstanceState == null) {
            addFragment(R.id.fragment_container, CurrentContactFragment.makeInstance());
        }
    }

    @OnClick(R.id.fab)
    public void handleAddFriends() {
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
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logOut();
                break;
        }
        return true;
    }

    private void logOut() {
        presenter.logout();
    }

    @Override
    public void onLogOut() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
