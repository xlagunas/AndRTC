package cat.xlagunas.data.common.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import io.reactivex.Flowable

@Dao
abstract class FriendDao {
    @Query("SELECT * FROM friend WHERE user_id = (:userId)")
    abstract fun getUserFriends(userId: Long): Flowable<List<FriendEntity>>

    @Query("SELECT * FROM friend WHERE user_id = (:userId) AND relationship_status = (:status)")
    abstract fun getUserFriendsWithRelationship(userId: Long, status: String): Flowable<List<FriendEntity>>

    @Query("SELECT * FROM friend WHERE user_id = (:userId) AND relationship_status NOT IN (:status)")
    abstract fun getUserFriendsExceptRelationship(userId: Long, vararg status: String): Flowable<List<FriendEntity>>

    @Query("SELECT * FROM friend WHERE name LIKE (:query) OR USERNAME LIKE (:query) OR email LIKE (:query)")
    abstract fun searchContacts(query: String): Flowable<List<FriendEntity>>

    @Query("UPDATE friend SET relationship_status = :status WHERE user_id = :userId AND id = :contactId")
    abstract fun update(userId: Long, contactId: Long, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(friendDbEntityList: List<FriendEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(friendDbEntity: FriendEntity)

    @Delete
    abstract fun delete(friendDbEntity: FriendEntity)

    @Delete
    abstract fun delete(friendDbEntityList: List<FriendEntity>)

    @Query("DELETE FROM friend WHERE user_id = (:userId)")
    abstract fun deleteData(userId: Long)

    @Transaction
    open fun overrideContacts(friendDbEntityList: List<FriendEntity>) {
        if (!friendDbEntityList.isEmpty()) {
            deleteData(friendDbEntityList[0].userId)
        }
        insert(friendDbEntityList)
    }
}