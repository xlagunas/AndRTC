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

import android.app.IntentService;
import android.content.Intent;

import javax.inject.Inject;

import cat.xlagunas.andrtc.CustomApplication;
import rx.Subscriber;
import timber.log.Timber;
import cat.xlagunas.andrtc.domain.interactor.RegisterGCMTokenUseCase;


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
        useCase.execute(new Subscriber() {
            @Override
            public void onCompleted() {
                Timber.d("GCM sending process finished");
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error sending GCM token");
            }

            @Override
            public void onNext(Object o) {
                Timber.d("GCM registration status: " + o.toString());
            }
        });
    }


}