package cat.xlagunas.andrtc.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xlagunas.andrtc.R;

/**
 * Created by xlagunas on 4/04/16.
 */
public abstract class GenericRegisterFragment extends BaseFragment {

    private final static String TAG = GenericRegisterFragment.class.getSimpleName();

    private OnFragmentChangeRequest listener;

    @Bind(R.id.register_fragment_container)
    ViewGroup contentLayout;

    public abstract @LayoutRes int getLayout();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (OnFragmentChangeRequest) context;
        } catch (ClassCastException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (listener != null){
            listener = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic_register, container, false);
        ButterKnife.bind(this, view);
        inflater.inflate(getLayout(), contentLayout, true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.button_next)
    public void onNextClicked(){
        Log.d(TAG, "Next clicked");
        if (listener != null){
            listener.onNext();
        }
    }

    public interface OnFragmentChangeRequest{
        void onNext();
        void onBack();
    }

}
