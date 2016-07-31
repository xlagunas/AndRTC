package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.CallRequestDataView;

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
}
