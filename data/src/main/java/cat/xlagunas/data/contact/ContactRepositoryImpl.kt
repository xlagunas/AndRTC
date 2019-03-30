package cat.xlagunas.data.contact

import android.annotation.SuppressLint
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.contact.ContactDetails
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.domain.contact.PhoneContactsDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class ContactRepositoryImpl
@Inject constructor(
    private val localContactDataSource: LocalContactDataSource,
    private val remoteContactDataSource: RemoteContactDataSource,
    private val phoneContactDataSource: PhoneContactsDataSource,
    private val cache: ContactCache,
    private val schedulers: RxSchedulers
) : ContactRepository {

    override fun requestFriendship(friend: Friend): Completable =
        remoteContactDataSource.requestFriendship(friend)
            .andThen(localContactDataSource.requestFriendship(friend))
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)

    override fun searchContact(query: String): Single<List<Friend>> =
        remoteContactDataSource.searchContacts(query)
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)

    @SuppressLint("CheckResult")
    override fun getContacts(): Flowable<List<Friend>> {
        cacheNeedsRefresh()
            .flatMapCompletable {
                remoteContactDataSource.getContactsAsSingle()
                    .flatMapCompletable(this::updateContacts)
            }
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
            .subscribe({}, Timber::e)

        return localContactDataSource.getContacts()
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun getPhoneContacts(): Flowable<List<ContactDetails>> {

        return phoneContactDataSource.getUserPhoneContacts()
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun forceUpdate(): Completable {
        return cache.invalidateCache()
            .andThen(remoteContactDataSource.getContactsAsSingle())
            .flatMapCompletable(this::updateContacts)
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
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

    private fun cacheNeedsRefresh(): Maybe<Boolean> {
        return cache.isCacheValid()
            .filter { isCacheValid -> !isCacheValid }
    }

    private fun updateContacts(friends: List<Friend>): Completable {
        return localContactDataSource.updateContacts(friends)
            .andThen(cache.updateCache())
    }
}