package cat.xlagunas.andrtc.view.activity;

import android.content.Intent;
import android.os.Bundle;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.gcm.RegistrationIntentService;


/**
 * Created by xlagunas on 9/03/16.
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(getApplicationContext(), RegistrationIntentService.class));
    }
}
