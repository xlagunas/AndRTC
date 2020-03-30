package cat.xlagunas.conference.domain

import cat.xlagunas.conference.data.dto.ConnectedUser
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import kotlinx.coroutines.channels.ReceiveChannel
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection

interface ConferenceRepository {

    fun joinRoom(onReceiveOfferMediaConstraints: MediaConstraints)
    suspend fun registerUser()
    suspend fun onNewUser(): ReceiveChannel<ConnectedUser>
    fun logoutRoom()
    fun createPeerConnection(userId: String): PeerConnection?
    fun createOffer(userId: String, mediaConstraints: MediaConstraints)
    fun getLocalRenderer(): ProxyVideoSink
    fun getRemoteRenderer(): ProxyVideoSink
}