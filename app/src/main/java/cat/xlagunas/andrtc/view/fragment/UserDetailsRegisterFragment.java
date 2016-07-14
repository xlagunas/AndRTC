package cat.xlagunas.andrtc.view.fragment;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewAnimationUtils;

import cat.xlagunas.andrtc.R;

/**
 * Created by xlagunas on 14/7/16.
 */
public class UserDetailsRegisterFragment extends GenericRegisterFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int cx = (int) nextButton.getX();
                int cy = (int) nextButton.getY();
                int width = getView().getWidth();
                int height = getView().getHeight();

                getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));

                float finalRadius = Math.max(width, height) / 2 + Math.max(width - cx, height - cy);
                Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
                anim.setDuration(300);
                anim.start();
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_user_details_register;
    }
}
