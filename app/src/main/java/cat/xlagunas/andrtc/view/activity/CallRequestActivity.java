package cat.xlagunas.andrtc.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.HasComponent;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.view.fragment.CallRequestBaseFragment;
import cat.xlagunas.andrtc.view.fragment.CalleeRequestFragment;
import cat.xlagunas.andrtc.view.fragment.CallerRequestFragment;

/**
 * Created by xlagunas on 25/7/16.
 */
public class CallRequestActivity extends AppCompatActivity implements HasComponent<UserComponent>, CallRequestBaseFragment.CallRequestListener{

    private final static String EXTRA_MODE = "type";
    public final static String EXTRA_FRIEND_ID = "friendId";
    private final static boolean EXTRA_MODE_CALLER = true;

    private boolean isCaller;


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

    public static Intent makeCalleeIntent(Context context){
        return makeIntent(context, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isCaller = getIntent().getBooleanExtra(EXTRA_MODE, false);
        String friendId = getIntent().getStringExtra(EXTRA_FRIEND_ID);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, isCaller ? CallerRequestFragment.makeInstance(friendId) : new CalleeRequestFragment())
                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCaller){
            //TODO REGISTER BROADCAST FROM NOTIFICATION
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCaller){
            //TODO UNREGISTER BROADCAST FROM NOTIFICATION
        }
    }

    @Override
    public UserComponent getComponent() {
        return CustomApplication.getApp(this).getUserComponent();
    }

    @Override
    public void onConferenceConfigured(String roomId) {
        startActivity(ConferenceActivity.startActivity(this, roomId));
        finish();
    }

    @Override
    public void onCancelConference() {
        finish();
    }
}
