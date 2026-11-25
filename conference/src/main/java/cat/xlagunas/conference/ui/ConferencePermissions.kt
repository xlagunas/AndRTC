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

/** What [ConferenceActivity] does with a runtime permissions result. */
internal sealed interface PermissionsOutcome {
    /** Every requested permission is granted: start the conference. */
    data object Start : PermissionsOutcome

    /** Denied (or cancelled), and the system can still ask: offer a retry. */
    data object ShowRetry : PermissionsOutcome

    /** Denied and the system won't ask again: send the user to the app settings. */
    data object ShowSettings : PermissionsOutcome

    /** No request is waiting for this result (for example after a recreation): do nothing. */
    data object Ignore : PermissionsOutcome
}

/**
 * Decides what to do with [result] for the [requested] permissions. A requested permission
 * missing from [result] counts as denied. An empty [result] means the request was cancelled
 * or interrupted. [shouldShowRationale] is only asked about denied permissions.
 */
internal fun permissionsOutcome(
    requested: Array<String>,
    result: Map<String, Boolean>,
    hasPendingRequest: Boolean,
    shouldShowRationale: (String) -> Boolean,
): PermissionsOutcome {
    if (!hasPendingRequest) return PermissionsOutcome.Ignore
    if (result.isEmpty()) return PermissionsOutcome.ShowRetry

    val denied = requested.filter { result[it] != true }
    return when {
        denied.isEmpty() -> PermissionsOutcome.Start
        denied.any(shouldShowRationale) -> PermissionsOutcome.ShowRetry
        else -> PermissionsOutcome.ShowSettings
    }
}
