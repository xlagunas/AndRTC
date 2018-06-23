package cat.xlagunas.data.contact

import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactDetails
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.domain.contact.PhoneContactsDataSource
import cat.xlagunas.domain.schedulers.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class ContactRepositoryImpl
@Inject constructor(
        private val localContactDataSource: LocalContactDataSource,
        private val remoteContactDataSource: RemoteContactDataSource,
        private val phoneContactDataSource: PhoneContactsDataSource,
        private val schedulers: RxSchedulers) : ContactRepository {

    override fun requestFriendship(friend: Friend): Completable =
            remoteContactDataSource.requestFriendship(friend)
                    .andThen(localContactDataSource.requestFriendship(friend))
                    .observeOn(schedulers.mainThread)
                    .subscribeOn(schedulers.io)

    override fun searchContact(query: String): Single<List<Friend>> =
            remoteContactDataSource.searchContacts(query)
                    .observeOn(schedulers.mainThread)
                    .subscribeOn(schedulers.io)

    override fun getContacts(): Flowable<List<Friend>> {
        if (isCacheOutDated()) {
            remoteContactDataSource.getContacts()
                    .flatMapCompletable { localContactDataSource.updateContacts(it) }
                    .observeOn(schedulers.mainThread)
                    .subscribeOn(schedulers.io)
                    .subscribe { Timber.d("Successfully added up-to-date contacts into database") }
        }

        return localContactDataSource.getContacts().observeOn(schedulers.mainThread).subscribeOn(schedulers.io)
    }

    override fun getPhoneContacts(): Flowable<List<ContactDetails>> {
        return phoneContactDataSource.getUserPhoneContacts()
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
    }

    //TODO created a cacheProvider that deals with the validity of data
    private fun isCacheOutDated(): Boolean {
        return true
    }


}