package cat.xlagunas.data.contact

import cat.xlagunas.data.common.converter.FriendConverter
import cat.xlagunas.data.common.net.FriendDto
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class RemoteContactDataSource
@Inject constructor(
        private val contactsApi: ContactsApi,
        private val friendConverter: FriendConverter) : ContactDataSource {

    override fun searchContacts(searchTerm: String): Single<List<Friend>> {
        return contactsApi.searchContact(searchTerm)
                .flattenAsObservable { items: List<FriendDto> -> items }
                .map(friendConverter::toFriend)
                .toList()
    }

    override fun requestFriendship(friend: Friend): Completable {
        return contactsApi.addContact(friend.friendId)
    }

    override fun getContacts(): Flowable<List<Friend>> {
        return contactsApi.listContacts()
                .flattenAsFlowable { t -> t }
                .map(friendConverter::toFriend)
                .toList().toFlowable()
    }
}
