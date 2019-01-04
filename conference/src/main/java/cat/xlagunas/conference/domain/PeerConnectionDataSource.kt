package cat.xlagunas.conference.domain

import cat.xlagunas.conference.data.NoOPVivSdpObserver
import cat.xlagunas.conference.data.VivPeerConnectionObserver
import cat.xlagunas.conference.data.VivSdpObserver
import cat.xlagunas.conference.data.WebRTCEventHandler
import org.webrtc.Logging
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class PeerConnectionDataSource @Inject constructor(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val webRTCEventHandler: WebRTCEventHandler,
    private val rtcConfiguration: PeerConnection.RTCConfiguration
) {

    private val peerConnectionMap = ConcurrentHashMap<String, PeerConnection>()

    fun getPeerConnection(userId: String): PeerConnection {
        return peerConnectionMap[userId]!!
    }

    fun addPeerConnection(userId: String, peerConnection: PeerConnection) {
        peerConnectionMap.put(userId, peerConnection)
    }

    fun createPeerConnection(userId: String): PeerConnection? {
        Timber.d("Creating peer connection per $userId")
        val peerObserver = VivPeerConnectionObserver(userId, webRTCEventHandler)
        val peerConnection = peerConnectionFactory.createPeerConnection(rtcConfiguration, peerObserver)!!
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)
        addPeerConnection(userId, peerConnection)
        return peerConnection
    }

    fun createOffer(
        userId: String,
        peerConnectionConstraints: MediaConstraints,
        block: (sessionDescription: SessionDescription) -> Unit
    ) {
        Timber.d("Creating offer for $userId")
        val sdpObserver = object : VivSdpObserver(userId) {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                //TODO PROBABLY WE DON'T NEED THIS LINE
//                getPeerConnection(userId).setLocalDescription(NoOPVivSdpObserver(userId), sessionDescription)
                block(sessionDescription)
            }
        }
        getPeerConnection(userId).createOffer(sdpObserver, peerConnectionConstraints)
    }

    fun handleRemoteAnswer(contactId: String, sessionDescription: SessionDescription) {
        Timber.d("Handling remote answer for $contactId")
        val peerConnection = getPeerConnection(contactId)
        peerConnection.setRemoteDescription(NoOPVivSdpObserver(contactId), sessionDescription)
    }

    fun handleRemoteOffer(
        contactId: String,
        sessionDescription: SessionDescription,
        peerConnectionConstraints: MediaConstraints,
        block: (sessionDescription: SessionDescription) -> Unit
    ) {
        Timber.d("Handling remote offer from $contactId")
        val peerConnection = getPeerConnection(contactId)
        val sdpObserver = object : VivSdpObserver(contactId) {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                Timber.d("Sending answer message to $contactId")
                //TODO MAYBE MAKES NO SENSE
//                peerConnection.setLocalDescription(
//                    NoOPVivSdpObserver(contactId),
//                    sessionDescription
//                )
                block(sessionDescription)
            }
        }
        peerConnection.setRemoteDescription(NoOPVivSdpObserver(contactId), sessionDescription)
        peerConnection.createAnswer(sdpObserver, peerConnectionConstraints)
    }
}