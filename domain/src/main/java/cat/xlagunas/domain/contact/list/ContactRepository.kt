package cat.xlagunas.domain.contact.list

import cat.xlagunas.domain.commons.Friend
import io.reactivex.Single

interface ContactRepository {

    fun findContacts(query: String): Single<List<Friend>>
}