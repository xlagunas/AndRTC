package cat.xlagunas.andrtc.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.view.fragment.AddContactFragment;

/**
 * Created by xlagunas on 14/03/16.
 */
public class AddContactsActivity extends BaseActivity implements HasComponent<UserComponent> {

    private static final String TAG = AddContactsActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            addFragment(R.id.fragment_container, AddContactFragment.makeInstance());
        }
    }


    @Override
    public UserComponent getComponent() {
        return CustomApplication.getApp(this).getUserComponent();
    }

    public static Intent buildAddContactsIntent(Context context) {
        Intent intent = new Intent(context, AddContactsActivity.class);

        return intent;
    }
}