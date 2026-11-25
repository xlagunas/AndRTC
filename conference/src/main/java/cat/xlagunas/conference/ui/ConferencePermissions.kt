package cat.xlagunas.conference.ui

import android.Manifest
import org.webrtc.MediaConstraints

private const val OFFER_AUDIO_KEY = "offerToReceiveAudio"
private const val OFFER_VIDEO_KEY = "offerToReceiveVideo"

/**
 * Runtime permissions needed for the media selected in [constraints]: CAMERA for video,
 * then RECORD_AUDIO for audio.
 */
internal fun requiredPermissions(constraints: MediaConstraints): Array<String> {
    fun isRequested(key: String) =
        constraints.mandatory.any {
            it.key == key && it.value.equals("true", ignoreCase = true)
        }

    return listOfNotNull(
        Manifest.permission.CAMERA.takeIf { isRequested(OFFER_VIDEO_KEY) },
        Manifest.permission.RECORD_AUDIO.takeIf { isRequested(OFFER_AUDIO_KEY) },
    ).toTypedArray()
}
