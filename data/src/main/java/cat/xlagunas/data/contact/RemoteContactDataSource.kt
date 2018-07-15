package cat.xlagunas.data.contact

import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.data.common.converter.FriendConverter
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

@OpenForTesting
class RemoteContactDataSource
@Inject constructor(
        private val contactsApi: ContactsApi,
        private val friendConverter: FriendConverter) : ContactDataSource {

    override fun searchContacts(searchTerm: String): Single<List<Friend>> {
        return contactsApi.searchContact(searchTerm)
                .flattenAsFlowable { items -> items }
                .map(friendConverter::toFriend)
                .toList()
    }

    override fun acceptContact(friend: Friend): Completable {
        return contactsApi.acceptContact(friend.friendId)
    }

    override fun rejectContact(friend: Friend): Completable {
        return contactsApi.rejectContact(friend.friendId)
    }

    override fun requestFriendship(friend: Friend): Completable {
        return contactsApi.addContact(friend.friendId)
    }

    override fun getContacts(): Flowable<List<Friend>> {
        return getContactsAsSingle().toFlowable()
    }

    fun getContactsAsSingle(): Single<List<Friend>> {
        return contactsApi.listContacts()
                .flattenAsFlowable { t -> t }
                .map(friendConverter::toFriend)
                .toList()
    }
}
