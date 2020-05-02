package cat.xlagunas.contact.domain

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ContactDataSource {

    fun searchContacts(searchTerm: String): Single<List<Friend>>

    fun requestFriendship(friend: Friend): Completable

    fun getContacts(): Flowable<List<Friend>>

    fun acceptContact(friend: Friend): Completable

    fun rejectContact(friend: Friend): Completable
}