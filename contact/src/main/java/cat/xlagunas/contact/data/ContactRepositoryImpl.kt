package cat.xlagunas.contact.data

import android.annotation.SuppressLint
import cat.xlagunas.contact.domain.ContactCache
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.core.scheduler.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class ContactRepositoryImpl
@Inject constructor(
    private val localContactDataSource: LocalContactDataSource,
    private val remoteContactDataSource: RemoteContactDataSource,
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
