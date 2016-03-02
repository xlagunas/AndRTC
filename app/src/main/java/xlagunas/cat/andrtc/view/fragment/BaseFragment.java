package xlagunas.cat.andrtc.view.fragment;

import android.app.Fragment;

import xlagunas.cat.andrtc.di.HasComponent;

/**
 * Created by xlagunas on 2/03/16.
 */
public class BaseFragment extends Fragment {

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }
}
