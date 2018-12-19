package cat.xlagunas.conference.data

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import timber.log.Timber

class VivSdpObserver(
    private val contactId: String,
    private val webRTCEventHandler: VivPeerConnectionObserver.WebRTCEventHandler,
    private val sessionDescriptionState: VivPeerConnectionObserver.SessionDescriptionState
) : SdpObserver {

    override fun onSetFailure(p0: String) {
        Timber.e("setOnFailure for peer $contactId created: $p0")
    }

    override fun onSetSuccess() {
        Timber.i("onSetSuccess called for peer $contactId")
    }

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        Timber.i("sessionDescription created for user $contactId")
        GlobalScope.launch {
            webRTCEventHandler.sessionDescriptionHandler.send(
                Triple(
                    contactId,
                    sessionDescription,
                    sessionDescriptionState
                )
            )
        }
    }

    override fun onCreateFailure(p0: String) {
        Timber.e("onCreateFailure for peer $contactId created: $p0")
    }
}