package cat.xlagunas.andrtc.di.components;

import cat.xlagunas.andrtc.di.UserScope;
import cat.xlagunas.andrtc.di.modules.UserModule;
import dagger.Subcomponent;

/**
 * Created by xlagunas on 2/03/16.
 */

@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {


}
