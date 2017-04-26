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


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.data.cache.UserCache;
import timber.log.Timber;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

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
        Timber.d("Token: " + token);
        userCache.setGCMToken(token);
        userCache.setGCMRegistrationStatus(false);
    }

}