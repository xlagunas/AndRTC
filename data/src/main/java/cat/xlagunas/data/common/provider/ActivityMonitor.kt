package cat.xlagunas.data.common.provider

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

class ActivityMonitor : Application.ActivityLifecycleCallbacks {

    companion object {
        @JvmStatic
        val NO_ACTIVITY = Any()
    }

    private val resumedActivity = BehaviorSubject.create<Any>()

    private var activity: Activity? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.d("onActivityCreated triggered")
        this.activity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.d("onActivityStarted triggered")
        this.activity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.d("onActivityResumed triggered")
        this.activity = activity
        resumedActivity.onNext(activity as Any)
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.d("onPauseEvent triggered")
        if (activity == resumedActivity.value) {
            Timber.d("onPause triggering no_activity")
            resumedActivity.onNext(NO_ACTIVITY)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.d("onActivityDestroyed triggered")
        this.activity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    fun resumedBaseActivityHot(): Observable<Activity> {
        return resumedActivity
            .filter(this::isActivity)
            .cast(Activity::class.java)
    }

    private fun isActivity(activityCandidate: Any): Boolean = activityCandidate is Activity
}