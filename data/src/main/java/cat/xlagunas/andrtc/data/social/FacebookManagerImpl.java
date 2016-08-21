package cat.xlagunas.andrtc.data.social;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by xlagunas on 20/8/16.
 */

public class FacebookManagerImpl implements FacebookManager {

    private static final String TAG = FacebookManagerImpl.class.getSimpleName();
    private final Activity activity;
    private final CallbackManager callbackManager;

    @Inject
    public FacebookManagerImpl(Activity activity, CallbackManager callbackManager) {
        this.activity = activity;
        this.callbackManager = callbackManager;
        Log.d(TAG, "CallbackManager " + callbackManager.toString());
    }

    public Observable<AccessToken> login() {

        return Observable.create(new Observable.OnSubscribe<AccessToken>() {
            @Override
            public void call(Subscriber<? super AccessToken> subscriber) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Profile.getCurrentProfile();
                            subscriber.onNext(loginResult.getAccessToken());
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onCancel() {
                            subscriber.onError(new Throwable("Canceled task"));
                        }

                        @Override
                        public void onError(FacebookException error) {
                            subscriber.onError(error);
                        }
                    });

                    LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends", "email"));
                } else {
                    Log.d(TAG, "Successfully logged in user");
                    subscriber.onNext(AccessToken.getCurrentAccessToken());
                    subscriber.onCompleted();
                }
            }
        });

    }

    @Override
    public Observable<JSONObject> requestProfileData(AccessToken accessToken) {

        return Observable.create(new Observable.OnSubscribe<JSONObject>() {

            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {

                GraphRequest.GraphJSONObjectCallback callback = (userJson, response) -> {
                    if (response.getError() != null) {
                        subscriber.onError(response.getError().getException());
                    } else {
                        subscriber.onNext(userJson);
                        subscriber.onCompleted();
                    }
                };

                generateProfileDataRequest(accessToken, callback);
            }

        });

    }

    public Observable<JSONArray> requestFriends(AccessToken accessToken) {
        return Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                GraphRequest request = GraphRequest.newMyFriendsRequest(accessToken, new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        if (response.getError() != null){
                            subscriber.onError(response.getError().getException());
                        } else {
                            subscriber.onNext(objects);
                            subscriber.onCompleted();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,first_name,middle_name,last_name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }
        });
    }

    private void generateProfileDataRequest(AccessToken accessToken, GraphRequest.GraphJSONObjectCallback callback) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, callback);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,first_name,middle_name,last_name,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }


}
