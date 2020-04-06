package cat.xlagunas.conference.domain

import cat.xlagunas.conference.domain.model.ProxyVideoSink
import cat.xlagunas.ws_messaging.model.Session
import kotlinx.coroutines.flow.Flow
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription

interface ConferenceRepository {

    fun joinRoom(roomId: String)
    suspend fun registerUser()
    suspend fun onNewUser(): Flow<Session>
    fun logoutRoom()
    fun createPeerConnection(user: Session, onIceCandidateGenerated: (onIceCandidateGenerated: Pair<Session,IceCandidate>) -> Unit) :PeerConnection?
    fun createOffer(user: Session, mediaConstraints: MediaConstraints): Flow<Pair<Session, SessionDescription>>
    fun handleRemoteOffer(contact: Session, mediaConstraints: MediaConstraints, sessionDescription: SessionDescription): Flow<Pair<Session, SessionDescription>>
    fun handleRemoteAnswer(contact: Session, sessionDescription: SessionDescription, onComplete: () -> Unit)
    fun addIceCandidate(contact: Session, iceCandidate: IceCandidate)
    fun getLocalRenderer(): ProxyVideoSink
    fun getRemoteRenderer(): ProxyVideoSink
}