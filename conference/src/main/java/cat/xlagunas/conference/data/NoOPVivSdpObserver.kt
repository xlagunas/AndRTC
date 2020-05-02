package cat.xlagunas.conference.data

import org.webrtc.SessionDescription
import timber.log.Timber

class NoOPVivSdpObserver(val userId: String) : VivSdpObserver(userId) {
    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        Timber.i("onCreateSuccess called for $userId")
    }
}
