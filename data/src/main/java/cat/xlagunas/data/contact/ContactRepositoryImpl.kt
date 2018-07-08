package cat.xlagunas.data.contact

import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactDetails
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.domain.contact.PhoneContactsDataSource
import cat.xlagunas.domain.schedulers.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.internal.functions.Functions
import timber.log.Timber
import javax.inject.Inject

class ContactRepositoryImpl
@Inject constructor(
        private val localContactDataSource: LocalContactDataSource,
        private val remoteContactDataSource: RemoteContactDataSource,
        private val phoneContactDataSource: PhoneContactsDataSource,
        private val cache: ContactCache,
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
        cache.isCacheValid().filter { validCache -> !validCache }.toSingle()
                .flatMapCompletable { getRemoteContactsAndUpdate() }
                .onErrorComplete(Functions.alwaysTrue())
                .doOnError { Timber.e(it, "Error requesting remote contacts") }
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
                .subscribe()

        return localContactDataSource.getContacts()
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
    }

    private fun getRemoteContactsAndUpdate(): Completable {
        return remoteContactDataSource.getContactsAsSingle()
                .flatMapCompletable { localContactDataSource.updateContacts(it) }
                .andThen(cache.updateCache())
    }

    override fun getPhoneContacts(): Flowable<List<ContactDetails>> {
        return phoneContactDataSource.getUserPhoneContacts()
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
    }

    override fun forceUpdate() {
        cache.invalidateCache()
                .andThen(getRemoteContactsAndUpdate())
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
                .subscribe()
    }

    override fun addContact(friend: Friend): Completable {
        return remoteContactDataSource.acceptContact(friend)
                .andThen(localContactDataSource.acceptContact(friend))
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
    }

    override fun rejectContact(friend: Friend): Completable {
        return remoteContactDataSource.rejectContact(friend)
                .andThen(localContactDataSource.rejectContact(friend))
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)
    }


}