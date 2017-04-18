package cat.xlagunas.andrtc.view.activity;

import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.social.GoogleManager;

/**
 * Created by xlagunas on 28/8/16.
 */
public abstract class SocialLoginActivity extends BaseActivity {

    @Inject
    GoogleManager googleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleManager.onCreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleManager.onActivityResult(requestCode, resultCode, data);
    }
}
