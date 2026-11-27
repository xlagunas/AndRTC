package cat.xlagunas.viv.e2e

import android.app.Activity
import android.os.Bundle
import cat.xlagunas.viv.dagger.VivApplication
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

/** Feeds a push message to the app as if FCM had delivered it, so E2E flows can trigger pushes. */
class E2ePushLinkActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching {
            val uri = intent?.data
            val data = pushData(uri?.getQueryParameter("type"), uri?.getQueryParameter("params"))
            if (data == null) {
                Timber.w("Ignoring e2e push link %s", uri)
            } else {
                val message = RemoteMessage.Builder("e2e@viv").setData(data).build()
                VivApplication.appComponent(this).pushMessageRouter().route(message)
                Timber.d("Routed e2e push %s", data)
            }
        }.onFailure { Timber.e(it, "Failed to handle the e2e push link") }
        finish()
    }
}
