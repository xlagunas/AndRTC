package cat.xlagunas.andrtc.di.components;

import cat.xlagunas.andrtc.di.ConferenceScope;
import cat.xlagunas.andrtc.di.modules.ConferenceModule;
import cat.xlagunas.andrtc.view.activity.ConferenceActivity;
import dagger.Subcomponent;

/**
 * Created by xlagunas on 4/8/16.
 */
@ConferenceScope
@Subcomponent(modules = {ConferenceModule.class})
public interface ConferenceComponent {
    void inject(ConferenceActivity activity);
}
