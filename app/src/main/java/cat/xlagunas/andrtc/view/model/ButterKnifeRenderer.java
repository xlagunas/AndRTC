package cat.xlagunas.andrtc.view.model;

import android.view.View;

import com.pedrogomez.renderers.Renderer;

/**
 * Created by xlagunas on 15/03/16.
 */
public abstract class ButterKnifeRenderer<T> extends Renderer<T> {

    @Override
    protected final void setUpView(View rootView) {
//        NO-OP for butterknife
    }

    @Override
    protected final void hookListeners(View rootView) {
//        NO-OP for butterknife
    }
}
