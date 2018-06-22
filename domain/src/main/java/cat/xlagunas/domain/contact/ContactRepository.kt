package cat.xlagunas.domain.contact

import cat.xlagunas.domain.commons.Friend
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ContactRepository {

    fun searchContact(query: String): Single<List<Friend>>

    fun requestFriendship(friendEntity: Friend): Completable

    fun getContacts(): Flowable<List<Friend>>
}