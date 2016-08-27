package cat.xlagunas.andrtc.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.OnClick;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.gcm.MyGcmListenerService;
import cat.xlagunas.andrtc.presenter.CallerRequestPresenter;
import cat.xlagunas.andrtc.view.activity.CallRequestActivity;
import cat.xlagunas.andrtc.view.activity.ConferenceActivity;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 25/7/16.
 */
public class CallerRequestFragment extends CallRequestBaseFragment {

    @Inject
    CallerRequestPresenter presenter;

    private BroadcastReceiver onCallAccepted;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class).inject(this);
        onCallAccepted = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                presenter.setCallId(intent.getStringExtra("roomId"));
                presenter.goToConference();
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
        presenter.setFriendId(getArguments().getString(CallRequestActivity.EXTRA_FRIEND_ID));
        presenter.init(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onCallAccepted,
                new IntentFilter(MyGcmListenerService.BROADCAST_CALL_ACCEPTED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onCallAccepted);
    }

    @OnClick(R.id.cancel_call)
    public void onCancelButton() {
        presenter.cancel();
    }

    public static Fragment makeInstance(String friendId) {
        CallerRequestFragment fragment = new CallerRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CallRequestActivity.EXTRA_FRIEND_ID, friendId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onBackPressed() {
        presenter.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
