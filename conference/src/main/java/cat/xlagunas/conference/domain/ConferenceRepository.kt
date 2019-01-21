package cat.xlagunas.conference.domain

import cat.xlagunas.conference.domain.model.Conferencee
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import kotlinx.coroutines.channels.ReceiveChannel
import org.webrtc.PeerConnection

interface ConferenceRepository {

    fun joinRoom()
    suspend fun getConnectedUsers(): ReceiveChannel<List<Conferencee>>
    fun createPeerConnection(userId: String): PeerConnection?
    fun createOffer(userId: String)
    suspend fun registerUser()
    fun logoutRoom()
    fun getLocalRenderer() : ProxyVideoSink
    fun getRemoteRenderer(): ProxyVideoSink
}