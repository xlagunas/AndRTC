package cat.xlagunas.andrtc.data.social;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import rx.Observable;

/**
 * Created by xlagunas on 28/8/16.
 */
public interface GoogleManager {

    void onCreate();

    Observable<GoogleSignInAccount> login();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
