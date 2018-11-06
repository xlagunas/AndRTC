package cat.xlagunas.conference.domain

import io.reactivex.Flowable

interface ConferenceRepository {

    fun joinRoom(roomId: String)
    fun getConnectedUsers() : Flowable<Conferencee>
    fun logoutRoom()
}