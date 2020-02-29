package cat.xlagunas.core.common

import android.content.Intent
import android.net.Uri
import cat.xlagunas.core.domain.entity.Call
import okhttp3.HttpUrl

object ContactUtils {
    fun generateRoomIntent(call: Call): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(HttpUrl.get("https://viv.cat/conference?roomId=${call.id}").toString())
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }
}