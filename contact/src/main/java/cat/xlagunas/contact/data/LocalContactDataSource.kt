package cat.xlagunas.contact.data

import cat.xlagunas.contact.domain.ContactDataSource
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.data.db.FriendEntity
import cat.xlagunas.core.data.net.Relationship
import cat.xlagunas.core.data.net.Relationship.ACCEPTED
import cat.xlagunas.core.data.net.Relationship.REQUESTED
import cat.xlagunas.core.domain.auth.AuthDataStore
import cat.xlagunas.core.domain.entity.Friend
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

@OpenForTesting
class LocalContactDataSource @Inject constructor(
    private val friendDao: cat.xlagunas.core.data.db.FriendDao,
    private val dataStore: AuthDataStore,
    private val friendConverter: cat.xlagunas.core.data.converter.FriendConverter
) : ContactDataSource {

    override fun requestFriendship(friend: Friend): Completable {
        return updateRelationship(friend, REQUESTED)
            .flatMapCompletable { Completable.fromAction { friendDao.insert(it) } }
    }

    override fun searchContacts(searchTerm: String): Single<List<Friend>> {
        throw UnsupportedOperationException("So far we don't need to search locally")
    }

    override fun getContacts(): Flowable<List<Friend>> {
        return friendDao.getUserFriends(dataStore.getCurrentUserId())
            .map { friendConverter.toFriendList(it) }
    }

    fun updateContacts(contactList: List<Friend>): Completable {
        return Flowable.fromIterable(contactList)
            .map { friendConverter.toFriendEntity(it, dataStore.getCurrentUserId()) }
            .toList()
            .flatMapCompletable { Completable.fromAction { friendDao.overrideContacts(it) } }
    }

    override fun acceptContact(friend: Friend): Completable {

        return updateRelationship(friend, ACCEPTED)
            .flatMapCompletable { Completable.fromAction { friendDao.insert(it) } }
    }

    override fun rejectContact(friend: Friend): Completable {
        return Single.just(friendConverter.toFriendEntity(friend, dataStore.getCurrentUserId()))
            .flatMapCompletable { Completable.fromAction { friendDao.delete(it) } }
    }

    private fun updateRelationship(friend: Friend, relationship: Relationship): Single<FriendEntity> {
        val updatedRelationship = friendConverter.updateRelationship(friend, relationship)
        val friendEntity = friendConverter.toFriendEntity(updatedRelationship, dataStore.getCurrentUserId())
        return Single.just(friendEntity)
    }
}