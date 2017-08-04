package cat.xlagunas.andrtc.data.social;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Created by xlagunas on 28/8/16.
 */

public class GoogleManagerImpl implements GoogleManager, GoogleApiClient.OnConnectionFailedListener {
    private static final int GOOGLE_SIGN_IN_CODE = 58759;

    private static final String TAG = GoogleManagerImpl.class.getSimpleName();

    private final Activity activity;
    private Observable.OnSubscribe<GoogleSignInAccount> subscribleListener;
    private LoginCallback loginCallback;
    private GoogleApiClient mGoogleApiClient;

    @Inject
    public GoogleManagerImpl(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onCreate() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

         mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage((FragmentActivity) activity,this)

                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public Observable<GoogleSignInAccount> login() {

        return Observable.create(new Observable.OnSubscribe<GoogleSignInAccount>() {
            @Override
            public void call(Subscriber<? super GoogleSignInAccount> subscriber) {
                loginCallback = new LoginCallback() {
                    @Override
                    public void onSuccess(GoogleSignInResult result) {
                        subscriber.onNext(result.getSignInAccount());
                    }

                    @Override
                    public void onError() {
                        Observable.error(new Throwable("Unable to retrieve the account"));
                    }
                };

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null) {
                loginCallback.onSuccess(result);
            } else {
                loginCallback.onError();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.d( connectionResult.getErrorMessage());
    }

    interface LoginCallback {
        void onSuccess(GoogleSignInResult result);
        void onError();
    }

}
