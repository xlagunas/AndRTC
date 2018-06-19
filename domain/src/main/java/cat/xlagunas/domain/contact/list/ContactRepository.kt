package cat.xlagunas.domain.contact.list

import cat.xlagunas.domain.commons.Friend
import io.reactivex.Completable
import io.reactivex.Single

interface ContactRepository {

    fun findContacts(query: String): Single<List<Friend>>

    fun requestFriendship(contactId: Long): Completable
}