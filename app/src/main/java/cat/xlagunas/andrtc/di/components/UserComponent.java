package cat.xlagunas.andrtc.di.components;

import cat.xlagunas.andrtc.di.UserScope;
import cat.xlagunas.andrtc.di.modules.ConferenceModule;
import cat.xlagunas.andrtc.di.modules.UserModule;
import cat.xlagunas.andrtc.gcm.MyGcmListenerService;
import cat.xlagunas.andrtc.gcm.RegistrationIntentService;
import cat.xlagunas.andrtc.view.activity.AddContactsActivity;
import cat.xlagunas.andrtc.view.activity.CallRequestActivity;
import cat.xlagunas.andrtc.view.activity.MainActivity;
import cat.xlagunas.andrtc.view.activity.RegisterActivity;
import cat.xlagunas.andrtc.view.fragment.AddContactFragment;
import cat.xlagunas.andrtc.view.fragment.CalleeRequestFragment;
import cat.xlagunas.andrtc.view.fragment.CallerRequestFragment;
import cat.xlagunas.andrtc.view.fragment.CurrentContactFragment;
import cat.xlagunas.andrtc.view.fragment.EmailPasswordRegisterFragment;
import cat.xlagunas.andrtc.view.fragment.UserDetailsRegisterFragment;
import cat.xlagunas.andrtc.view.fragment.UsernamePasswordFragment;
import dagger.Subcomponent;

/**
 * Created by xlagunas on 2/03/16.
 */

@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {

    void inject(RegisterActivity activity);
    void inject(RegistrationIntentService service);
    void inject(AddContactsActivity activity);

    void inject(AddContactFragment fragment);
    void inject(CurrentContactFragment fragment);

    void inject(UsernamePasswordFragment fragment);
    void inject(UserDetailsRegisterFragment fragment);
    void inject(EmailPasswordRegisterFragment fragment);

    void inject(CallerRequestFragment fragment);
    void inject(CalleeRequestFragment fragment);

    void inject(CallRequestActivity activity);

    void inject(MyGcmListenerService service);

    ConferenceComponent plus(ConferenceModule module);

}
