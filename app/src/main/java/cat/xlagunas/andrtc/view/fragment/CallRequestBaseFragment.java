package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.CallRequestDataView;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 25/7/16.
 */
public abstract class CallRequestBaseFragment extends BaseFragment implements CallRequestDataView {


    private static final String TAG = CallRequestBaseFragment.class.getSimpleName();

    @Bind(R.id.caller_image)
    protected ImageView callerImage;

    @Bind(R.id.caller_name)
    protected TextView callerName;

    @Bind(R.id.accept_call)
    protected Button accept;

    @Bind(R.id.cancel_call)
    protected Button cancel;

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
        Snackbar.make(getView(), "Error: "+e.getMessage(), Snackbar.LENGTH_SHORT).show();
        Log.e(TAG, "Error", e);
    }

    @Override
    public void updateUserData(Friend friend) {
        Glide.with(this).load(friend.getThumbnail()).centerCrop().into(callerImage);
        StringBuilder builder = new StringBuilder(friend.getName())
                .append(" "+friend.getSurname());
        if (friend.getLastSurname() != null && !friend.getLastSurname().isEmpty()){
            builder.append(" "+friend.getLastSurname());
        }

        getActivity().setTitle(builder.toString());
    }
}
