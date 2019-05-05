package cat.xlagunas.contact.data

import cat.xlagunas.contact.domain.ContactDataSource
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.data.net.Relationship.ACCEPTED
import cat.xlagunas.core.data.net.Relationship.REQUESTED
import cat.xlagunas.core.domain.entity.Friend
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

@OpenForTesting
class LocalContactDataSource @Inject constructor(
    private val friendDao: cat.xlagunas.core.data.db.FriendDao,
    private val userDao: cat.xlagunas.core.data.db.UserDao,
    private val friendConverter: cat.xlagunas.core.data.converter.FriendConverter
) : ContactDataSource {

    override fun requestFriendship(friend: Friend): Completable {

        return userDao.user
            .map {
                friendConverter.toFriendEntity(
                    friendConverter.updateRelationship(friend, REQUESTED),
                    it.id!!
                )
            }
            .flatMapCompletable { Completable.fromAction { friendDao.insert(it) } }
    }

    override fun searchContacts(searchTerm: String): Single<List<Friend>> {
        throw UnsupportedOperationException("So far we don't need to search locally")
    }

    override fun getContacts(): Flowable<List<Friend>> {
        return userDao.user
            .flatMapPublisher { friendDao.getUserFriends(it.id!!) }
            .map { friendConverter.toFriendList(it) }
    }

    fun updateContacts(contactList: List<Friend>): Completable {

        return Flowable.combineLatest(userDao.user.toFlowable(), Flowable.fromIterable(contactList),
            BiFunction { user: cat.xlagunas.core.data.db.UserEntity, friend: Friend ->
                friendConverter.toFriendEntity(
                    friend,
                    user.id!!
                )
            })
            .toList()
            .flatMapCompletable { Completable.fromAction { friendDao.overrideContacts(it) } }
    }

    override fun acceptContact(friend: Friend): Completable {
        return userDao.user
            .map {
                friendConverter.toFriendEntity(
                    friendConverter.updateRelationship(friend, ACCEPTED),
                    it.id!!
                )
            }
            .flatMapCompletable { Completable.fromAction { friendDao.insert(it) } }
    }

    override fun rejectContact(friend: Friend): Completable {
        return userDao.user
            .map { friendConverter.toFriendEntity(friend, it.id!!) }
            .flatMapCompletable { Completable.fromAction { friendDao.delete(it) } }
    }
}