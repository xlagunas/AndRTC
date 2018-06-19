package cat.xlagunas.data.contact.list

import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.list.ContactRepository
import cat.xlagunas.domain.schedulers.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ContactRepositoryImpl
@Inject constructor(
        private val remoteContactsDataSource: RemoteContactsDataSource,
        private val schedulers: RxSchedulers) : ContactRepository {

    override fun requestFriendship(contactId: Long): Completable =
            remoteContactsDataSource.requestFriendship(contactId)
                    .observeOn(schedulers.mainThread)
                    .subscribeOn(schedulers.io)

    override fun findContacts(query: String): Single<List<Friend>> =
            remoteContactsDataSource.searchContacts(query)
                    .observeOn(schedulers.mainThread)
                    .subscribeOn(schedulers.io)

}