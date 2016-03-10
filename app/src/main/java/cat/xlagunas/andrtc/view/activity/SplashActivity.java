package cat.xlagunas.andrtc.view.activity;

import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;


import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.data.UserEntity;

/**
 * Created by xlagunas on 8/03/16.
 */
public class SplashActivity extends BaseActivity {

    private final static long WAITING_TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getApplicationComponent().userCache().getUser()
                .delaySubscription(WAITING_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UserEntity>() {
                    @Override
                    public void onCompleted() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onNext(UserEntity userEntity) {

                    }
                });
    }
}
