package cat.xlagunas.contact.domain

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ContactRepository {

    fun searchContact(query: String): Single<List<Friend>>

    fun requestFriendship(friend: Friend): Completable

    fun getContacts(): Flowable<List<Friend>>

    fun forceUpdate(): Completable

    fun addContact(friend: Friend): Completable

    fun rejectContact(friend: Friend): Completable
}
