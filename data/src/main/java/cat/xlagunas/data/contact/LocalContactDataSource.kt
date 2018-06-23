package cat.xlagunas.data.contact

import cat.xlagunas.data.common.converter.FriendConverter
import cat.xlagunas.data.common.db.FriendDao
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.data.common.db.UserEntity
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class LocalContactDataSource @Inject constructor(private val friendDao: FriendDao,
                                                 private val userDao: UserDao,
                                                 private val friendConverter: FriendConverter) : ContactDataSource {

    override fun requestFriendship(friend: Friend): Completable {
        return userDao.user.map { friendConverter.toFriendEntity(friend, it.id!!) }
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
        val friendEntityFlowable = Flowable.combineLatest(
                userDao.user.toFlowable(),
                Flowable.fromIterable(contactList),
                BiFunction { user: UserEntity, friend: Friend -> friendConverter.toFriendEntity(friend, user.id!!) })

        return friendEntityFlowable.toList()
                .flatMapCompletable { Completable.fromAction { friendDao.insert(it) } }
    }
}