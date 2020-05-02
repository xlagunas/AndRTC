package cat.xlagunas.viv

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TopActivityProvider : Application.ActivityLifecycleCallbacks {

    var topActivity: AppCompatActivity? = null
        private set

    override fun onActivityResumed(activity: Activity) {
        topActivity = activity as AppCompatActivity
    }

    override fun onActivityPaused(activity: Activity) {
        topActivity = null
    }

    override fun onActivityStarted(activity: Activity?) {}
    override fun onActivityDestroyed(activity: Activity?) {}
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}
}
