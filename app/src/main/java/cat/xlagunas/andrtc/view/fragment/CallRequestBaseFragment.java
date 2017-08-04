package cat.xlagunas.andrtc.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.CallRequestDataView;
import timber.log.Timber;
import cat.xlagunas.andrtc.domain.Friend;

/**
 * Created by xlagunas on 25/7/16.
 */
public abstract class CallRequestBaseFragment extends BaseFragment implements CallRequestDataView {

    private static final String TAG = CallRequestBaseFragment.class.getSimpleName();

    @BindView(R.id.caller_image)
    protected ImageView callerImage;

    @BindView(R.id.caller_name)
    protected TextView callerName;

    @BindView(R.id.accept_call)
    protected Button accept;

    @BindView(R.id.cancel_call)
    protected Button cancel;

    protected CallRequestListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CallRequestListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity must implement CallRequestListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_call_request, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void hideAcceptCallButton() {
        accept.setVisibility(View.GONE);
    }

    @Override
    public void setOnError(Throwable e) {
        Snackbar.make(getView(), "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
        Timber.e(e, "Error");
    }

    @Override
    public void updateUserData(Friend friend) {
        Glide.with(this).load(friend.getThumbnail()).centerCrop().into(callerImage);
        StringBuilder builder = new StringBuilder(friend.getName())
                .append(" " + friend.getSurname());
        if (friend.getLastSurname() != null && !friend.getLastSurname().isEmpty()) {
            builder.append(" " + friend.getLastSurname());
        }

        getActivity().setTitle(builder.toString());
    }

    public void startConference(String roomId) {
        Timber.d("Starting conference with Id: " + roomId);
        listener.onConferenceConfigured(roomId);
    }

    public void cancelConference() {
        Timber.d("Canceling conference");
        listener.onCancelConference();
    }

    public interface CallRequestListener {
        void onConferenceConfigured(String roomId);

        void onCancelConference();
    }

    public abstract void onBackPressed();
}
