package cat.xlagunas.viv.contact

import android.content.Context
import android.content.Intent
import android.net.Uri
import okhttp3.HttpUrl

object ContactUtils {
    fun generateCallIntent(roomId: String, context: Context): Intent {
        val url = HttpUrl.get("https://viv.cat/conference?roomId=$roomId")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.`package` = context.packageName
        intent.data = Uri.parse(url.toString())
        return intent
    }
}