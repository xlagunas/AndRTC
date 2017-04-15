package cat.xlagunas.andrtc.gcm; /**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.data.cache.UserCache;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.interactor.RegisterGCMTokenUseCase;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    @Inject
    RegisterGCMTokenUseCase useCase;

    @Inject
    UserCache userCache;

    @Override
    public void onCreate() {
        super.onCreate();
        CustomApplication.getApp(this).getUserComponent().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Log.d(TAG, "Token: " + token);
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