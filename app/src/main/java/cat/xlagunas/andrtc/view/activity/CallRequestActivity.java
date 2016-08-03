package cat.xlagunas.andrtc.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.view.fragment.CalleeRequestFragment;
import cat.xlagunas.andrtc.view.fragment.CallerRequestFragment;

/**
 * Created by xlagunas on 25/7/16.
 */
public class CallRequestActivity extends AppCompatActivity implements HasComponent<UserComponent>{

    private final static String EXTRA_MODE = "type";
    public final static String EXTRA_FRIEND_ID = "friendId";
    public final static String EXTRA_ROOM_ID = "roomId";

    private boolean isCaller;

    @Bind(R.id.toolbar)
    Toolbar toolbar;


    private static Intent makeIntent(Context context, boolean isCaller){
        Intent intent = new Intent(context, CallRequestActivity.class);
        intent.putExtra(EXTRA_MODE, isCaller);
        return intent;
    }

    public static Intent makeCallerIntent(Context context, String friendId){
        Intent intent = makeIntent(context, true);
        intent.putExtra(EXTRA_FRIEND_ID, friendId);
        return intent;
    }

    public static Intent makeCalleeIntent(Context context, String friendId, String roomId){
        Intent intent = makeIntent(context, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_FRIEND_ID, friendId);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isCaller = getIntent().getBooleanExtra(EXTRA_MODE, false);
        String friendId = getIntent().getStringExtra(EXTRA_FRIEND_ID);
        String roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, isCaller
                        ? CallerRequestFragment.makeInstance(friendId)
                        : CalleeRequestFragment.makeInstance(friendId, roomId))
                .commit();

    }

    @Override
    public UserComponent getComponent() {
        return CustomApplication.getApp(this).getUserComponent();
    }
}
