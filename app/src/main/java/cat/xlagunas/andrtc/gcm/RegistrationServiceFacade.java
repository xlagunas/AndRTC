package cat.xlagunas.andrtc.gcm;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import cat.xlagunas.andrtc.ServiceFacade;

/**
 * Created by xlagunas on 12/03/16.
 */
public class RegistrationServiceFacade implements ServiceFacade {

    private Context context;

    @Inject
    public RegistrationServiceFacade(Context context){
        this.context = context;
    }

    @Override
    public void startService() {
        context.startService(new Intent(context, RegistrationIntentService.class));
    }
}
