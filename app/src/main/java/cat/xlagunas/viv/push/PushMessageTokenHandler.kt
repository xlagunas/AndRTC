package cat.xlagunas.viv.push

import com.google.firebase.iid.FirebaseInstanceIdService
import dagger.android.AndroidInjection
import javax.inject.Inject

class PushMessageTokenHandler : FirebaseInstanceIdService() {

    @Inject
    lateinit var pushTokenPresenter: PushTokenPresenter

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onTokenRefresh() {
        pushTokenPresenter.registerToken()
    }
}