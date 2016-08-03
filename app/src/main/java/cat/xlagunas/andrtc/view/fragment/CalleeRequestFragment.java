package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import javax.inject.Inject;

import butterknife.OnClick;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.CalleeRequestPresenter;

/**
 * Created by xlagunas on 25/7/16.
 */
public class CalleeRequestFragment extends CallRequestBaseFragment {
    private final static String EXTRA_CALLER_ID = "userId";
    private final static String EXTRA_ROOM_ID = "callerId";

    private static final String TAG = CalleeRequestFragment.class.getSimpleName();


    @Inject
    CalleeRequestPresenter presenter;


    public static CalleeRequestFragment makeInstance(String callerId, String roomId){
        CalleeRequestFragment fragment = new CalleeRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CALLER_ID, callerId);
        bundle.putString(EXTRA_ROOM_ID, roomId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class).inject(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
        String friendId = getArguments().getString(EXTRA_CALLER_ID);
        String roomId = getArguments().getString(EXTRA_ROOM_ID);
        presenter.setFriendId(friendId);
        presenter.setCallId(roomId);
        presenter.init(false);
    }

    @Override
    public void startConference(String roomId) {
        Log.d(TAG, "Starting conference with Id: "+roomId);
        Snackbar.make(getView(), "Starting conference with id"+roomId, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.accept_call)
    public void onAcceptButton(){
        presenter.accept();
    }

    @OnClick(R.id.cancel_call)
    public void onCancelButton(){
        presenter.cancel();
    }

    @Override
    public void cancelConference() {
        Log.d(TAG, "Canceling conference");
        Snackbar.make(getView(), "Canceling conference", Snackbar.LENGTH_SHORT).show();
        getActivity().finish();
    }
}
