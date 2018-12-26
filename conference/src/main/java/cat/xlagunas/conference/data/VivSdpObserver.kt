package cat.xlagunas.conference.data

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import timber.log.Timber

abstract class VivSdpObserver(
    internal val contactId: String
) : SdpObserver {

    override fun onSetFailure(p0: String) {
        Timber.e("setOnFailure for peer $contactId created: $p0")
    }

    override fun onSetSuccess() {
        Timber.i("onSetSuccess called for peer $contactId")
    }

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
    }

    override fun onCreateFailure(p0: String) {
        Timber.e("onCreateFailure for peer $contactId created: $p0")
    }
}