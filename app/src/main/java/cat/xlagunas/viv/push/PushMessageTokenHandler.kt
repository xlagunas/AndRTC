package cat.xlagunas.viv.push

import cat.xlagunas.viv.commons.di.VivApplication
import com.google.firebase.iid.FirebaseInstanceIdService

class PushMessageTokenHandler : FirebaseInstanceIdService() {

    private lateinit var pushTokenViewModel: PushTokenViewModel

    override fun onCreate() {
        super.onCreate()
        pushTokenViewModel = (application as VivApplication).viewModelFactory.create(PushTokenViewModel::class.java)
    }

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        pushTokenViewModel.registerToken()
    }

}