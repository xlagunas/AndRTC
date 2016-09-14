package cat.xlagunas.andrtc.data.social;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import javax.inject.Inject;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xlagunas on 20/8/16.
 */

public class FacebookManagerImpl implements FacebookManager {

    private static final String TAG = FacebookManagerImpl.class.getSimpleName();
    private final Activity activity;
    private final CallbackManager callbackManager;

    private final String REQUESTED_FIELDS = "id,name,link,gender,first_name,middle_name,last_name,email,picture";

    @Inject
    public FacebookManagerImpl(Activity activity) {
        this.activity = activity;
        this.callbackManager = CallbackManager.Factory.create();
    }

    public Observable<AccessToken> login() {
        return Observable.fromAsync(new Action1<AsyncEmitter<AccessToken>>() {
            @Override
            public void call(AsyncEmitter<AccessToken> objectAsyncEmitter) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    FacebookCallback<LoginResult> loginResultFacebookCallback = generateAsyncLogin(objectAsyncEmitter);
                    LoginManager.getInstance().registerCallback(callbackManager, loginResultFacebookCallback);
                    LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends", "email"));
                } else {
                    Log.d(TAG, "Successfully logged in user");
                    objectAsyncEmitter.onNext(AccessToken.getCurrentAccessToken());
                    objectAsyncEmitter.onCompleted();
                }
            }
        }, AsyncEmitter.BackpressureMode.BUFFER).observeOn(Schedulers.io());

    }

    @Override
    public Observable<JSONObject> requestProfileData(AccessToken accessToken) {
        return Observable.fromCallable(() -> generateProfileDataRequest(accessToken)
                .getJSONObject());
    }

    public Observable<JSONArray> requestFriends(AccessToken accessToken) {
        return Observable.fromCallable(() -> generateFriendsDataRequest(accessToken)
                .getJSONArray());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode() == requestCode) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private GraphResponse generateProfileDataRequest(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, null);
        Bundle parameters = new Bundle();
        parameters.putString("fields", REQUESTED_FIELDS);
        request.setParameters(parameters);
        return request.executeAndWait();
    }

    private GraphResponse generateFriendsDataRequest(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(accessToken, null);
        Bundle parameters = new Bundle();
        parameters.putString("fields", REQUESTED_FIELDS);
        request.setParameters(parameters);
        return request.executeAndWait();
    }

    private FacebookCallback<LoginResult> generateAsyncLogin(AsyncEmitter emitter) {
        FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile.getCurrentProfile();
                emitter.onNext(loginResult.getAccessToken());
                emitter.onCompleted();
            }

            @Override
            public void onCancel() {
                emitter.onError(new Throwable("Canceled task"));
            }

            @Override
            public void onError(FacebookException error) {
                emitter.onError(error);
            }
        };

        return loginResultFacebookCallback;
    }

    @Override
    public Action0 logOut(){
        return () -> LoginManager.getInstance().logOut();
    }

}
