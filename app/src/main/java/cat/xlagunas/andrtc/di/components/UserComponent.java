package cat.xlagunas.andrtc.di.components;

import cat.xlagunas.andrtc.di.UserScope;
import cat.xlagunas.andrtc.di.modules.UserModule;
import cat.xlagunas.andrtc.gcm.RegistrationIntentService;
import cat.xlagunas.andrtc.view.activity.AddContactsActivity;
import cat.xlagunas.andrtc.view.activity.MainActivity;
import cat.xlagunas.andrtc.view.fragment.AddContactFragment;
import dagger.Subcomponent;

/**
 * Created by xlagunas on 2/03/16.
 */

@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {

    void inject(MainActivity activity);
    void inject(RegistrationIntentService service);
    void inject(AddContactsActivity activity);
    void inject(AddContactFragment fragment);
}
