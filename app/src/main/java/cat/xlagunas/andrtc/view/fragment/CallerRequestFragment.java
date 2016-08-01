package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.OnClick;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.CallerRequestPresenter;
import cat.xlagunas.andrtc.view.activity.CallRequestActivity;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 25/7/16.
 */
public class CallerRequestFragment extends CallRequestBaseFragment {

    @Inject
    CallerRequestPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class).inject(this);
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
    }

    @Override
    public void startConference(String confId) {
        Snackbar.make(getView(), "STARTING CONFERENCE", Snackbar.LENGTH_LONG).show();
    }

    @Override
    @OnClick(R.id.cancel_call)
    public void cancelConference() {
        Snackbar.make(getView(), "CANCELLING CONFERENCE", Snackbar.LENGTH_LONG).show();
        getActivity().finish();
    }


    public static Fragment makeInstance(String friendId) {
        CallerRequestFragment fragment = new CallerRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CallRequestActivity.EXTRA_FRIEND_ID, friendId);
        fragment.setArguments(bundle);
        return fragment;
    }
}
