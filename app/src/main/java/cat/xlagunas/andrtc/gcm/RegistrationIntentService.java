package cat.xlagunas.andrtc.gcm; /**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import javax.inject.Inject;

import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.ServiceFacade;
import retrofit2.Response;
import rx.Observer;
import rx.Subscriber;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.interactor.RegisterGCMTokenUseCase;


public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    @Inject
    RegisterGCMTokenUseCase useCase;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CustomApplication.getApp(this).getUserComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token);

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }

    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Log.d(TAG, "Token: "+token);
        useCase.setToken(token);
        useCase.execute(new TokenSubscriber());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        useCase.unsubscribe();
    }

    static class TokenSubscriber extends DefaultSubscriber {
        @Override
        public void onCompleted() {
            super.onCompleted();
            Log.d(TAG, "Successfully registered");
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "Error registering token", e);
        }
    }
}