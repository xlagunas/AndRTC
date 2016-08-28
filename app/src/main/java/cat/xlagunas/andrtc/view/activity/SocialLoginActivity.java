package cat.xlagunas.andrtc.view.activity;

import android.content.Intent;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.social.FacebookManager;

/**
 * Created by xlagunas on 28/8/16.
 */
public abstract class SocialLoginActivity extends BaseActivity {

    @Inject
    FacebookManager facebookManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookManager.onActivityResult(requestCode, resultCode, data);
    }
}
