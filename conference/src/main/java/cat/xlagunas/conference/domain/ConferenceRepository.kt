package cat.xlagunas.conference.domain

import cat.xlagunas.conference.domain.model.Conferencee
import kotlinx.coroutines.channels.ReceiveChannel

interface ConferenceRepository {

    fun joinRoom()
    suspend fun getConnectedUsers(): ReceiveChannel<List<Conferencee>>
    fun logoutRoom()
}