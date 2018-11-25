package cat.xlagunas.conference.domain

import io.reactivex.Flowable

interface ConferenceRepository {

    fun joinRoom()
    fun getConnectedUsers(): Flowable<Conferencee>
    fun logoutRoom()
}