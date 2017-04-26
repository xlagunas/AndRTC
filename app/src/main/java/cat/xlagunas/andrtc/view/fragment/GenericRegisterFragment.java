package cat.xlagunas.andrtc.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cat.xlagunas.andrtc.R;
import timber.log.Timber;

/**
 * Created by xlagunas on 4/04/16.
 */
public abstract class GenericRegisterFragment extends BaseFragment {

    private final static String TAG = GenericRegisterFragment.class.getSimpleName();

    private OnFragmentChangeRequest listener;

    @BindView(R.id.button_next)
    protected Button nextButton;

    ViewGroup contentLayout;
    protected Unbinder unBinder;

    public abstract @LayoutRes
    int getLayout();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnFragmentChangeRequest) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (listener != null) {
            listener = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic_register, container, false);
        contentLayout = (ViewGroup) view.findViewById(R.id.register_fragment_container);
        inflater.inflate(getLayout(), contentLayout, true);
        unBinder = ButterKnife.bind(this, view);
        getNextButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClicked();
            }
        });

        //there's an option to supply own next button, if so, remove the current one
        nextButton.setVisibility(getNextButton().getId() != nextButton.getId() ? View.GONE : View.VISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }

    @OnClick(R.id.button_next)
    public void onNextClicked() {
        Timber.d("Next clicked");
        if (listener != null) {
            listener.onNext();
        }
    }

    protected void setError(TextInputLayout textInputLayout, String message) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(message);
    }

    public View getNextButton() {
        return nextButton;
    }

    public interface OnFragmentChangeRequest {
        void onNext();

        void onBack();
    }
}
