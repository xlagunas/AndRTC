package cat.xlagunas.andrtc;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.support.test.runner.AndroidJUnitRunner;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.FULL_WAKE_LOCK;
import static android.os.PowerManager.ON_AFTER_RELEASE;

public final class AndroidRunner extends AndroidJUnitRunner {
    private PowerManager.WakeLock wakeLock;

    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        // Inform the app we are an instrumentation test before the object graph is initialized.

        Context app = getTargetContext().getApplicationContext();

        String name = AndroidRunner.class.getSimpleName();
        // Unlock the device so that the tests can input keystrokes.
        KeyguardManager keyguard = (KeyguardManager) app.getSystemService(KEYGUARD_SERVICE);
        if (keyguard.isDeviceLocked()) {
            keyguard.newKeyguardLock(name).disableKeyguard();
        }
        // Wake up the screen.
        PowerManager power = (PowerManager) app.getSystemService(POWER_SERVICE);
        wakeLock = power.newWakeLock(FULL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE, name);
        wakeLock.acquire();

        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        wakeLock.release();
    }
}