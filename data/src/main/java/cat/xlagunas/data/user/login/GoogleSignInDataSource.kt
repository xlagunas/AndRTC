package cat.xlagunas.data.user.login

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import cat.xlagunas.data.common.provider.ActivityMonitor
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.schedulers.RxSchedulers
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.reactivex.Flowable
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class GoogleSignInDataSource @Inject constructor(
    private val activityMonitor: ActivityMonitor,
    private val schedulers: RxSchedulers
) : LifecycleObserver {

    companion object {
        const val RC_SIGN_IN = 4004
    }

    private lateinit var client: GoogleSignInClient

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        this.activityMonitor.resumedBaseActivityHot().take(1)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.mainThread)
            .subscribe(this::initGoogleAuthentication, Timber::e)
    }

    private fun initGoogleAuthentication(activity: Activity) {
        Timber.d("Initializing google sign in client")
        val signingOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        client = GoogleSignIn.getClient(activity, signingOptions)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Timber.d("Checking if user is already logged in")
        this.activityMonitor.resumedBaseActivityHot().take(1)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.mainThread)
            .subscribe(this::checkUserAlreadyLoggedIn, Timber::e)
    }

    private fun checkUserAlreadyLoggedIn(activity: Activity) {
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            Timber.w("User is already signed in %s", account.email)
        }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>): Flowable<User> {
        return try {
            val account = completedTask.getResult(ApiException::class.java)
            return if (account != null) {
                Flowable.just(
                    User(
                        account.email!!,
                        account.givenName!!,
                        account.familyName!!,
                        account.email!!,
                        account.photoUrl.toString(),
                        UUID.randomUUID().toString()
                    )
                )
            } else {
                return Flowable.empty()
            }
        } catch (e: ApiException) {
            Flowable.just(User("This", "was", "a", "user", "who", "didn't"))
        }
    }

    fun signIn() {
        Timber.d("Starting register")
        this.activityMonitor.resumedBaseActivityHot().take(1)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.mainThread)
            .subscribe({ activity -> activity.startActivityForResult(client.signInIntent, RC_SIGN_IN) }, Timber::e)
    }
}