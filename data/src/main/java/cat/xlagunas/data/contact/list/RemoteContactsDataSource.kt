package cat.xlagunas.data.contact.list

import cat.xlagunas.data.common.converter.FriendConverter
import cat.xlagunas.data.common.net.FriendDto
import cat.xlagunas.domain.commons.Friend
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class RemoteContactsDataSource
@Inject constructor(
        private val contactsApi: ContactsApi,
        private val friendConverter: FriendConverter) {

    fun searchContacts(searchTerm: String): Single<List<Friend>> {
        return contactsApi.searchContact(searchTerm)
                .flattenAsObservable { items: List<FriendDto> -> items }
                .map(friendConverter::toFriend)
                .toList()
    }

    fun requestFriendship(contactId: Long): Completable {
        return contactsApi.addContact(contactId)
    }
}
