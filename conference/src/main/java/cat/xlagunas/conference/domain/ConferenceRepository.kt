package cat.xlagunas.conference.domain

import cat.xlagunas.conference.data.dto.ConnectedUser
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import kotlinx.coroutines.channels.ReceiveChannel
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection

interface ConferenceRepository {

    suspend fun onNewUser(): ReceiveChannel<ConnectedUser>
    fun createPeerConnection(userId: String): PeerConnection?
    fun createOffer(userId: String, mediaConstraints: MediaConstraints)
    suspend fun registerUser()
    fun logoutRoom()
    fun getLocalRenderer(): ProxyVideoSink
    fun getRemoteRenderer(): ProxyVideoSink
    fun joinRoom(onReceiveOfferMediaConstraints: MediaConstraints)
}