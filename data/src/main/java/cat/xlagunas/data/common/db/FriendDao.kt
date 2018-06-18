package cat.xlagunas.data.common.db

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface FriendDao {
    @Query("SELECT * FROM friend WHERE user_id = (:userId)")
    fun getUserFriends(userId: Long): Flowable<List<cat.xlagunas.data.common.db.FriendEntity>>

    @Query("SELECT * FROM friend WHERE user_id = (:userId) AND relationship_status = (:status)")
    fun getUserFriendsWithRelationship(userId: Long, status: String): Flowable<List<cat.xlagunas.data.common.db.FriendEntity>>

    @Query("SELECT * FROM friend WHERE user_id = (:userId) AND relationship_status NOT IN (:status)")
    fun getUserFriendsExceptRelationship(userId: Long, vararg status: String): Flowable<List<cat.xlagunas.data.common.db.FriendEntity>>

    @Query("SELECT * FROM friend WHERE name LIKE (:query) OR USERNAME LIKE (:query) OR email LIKE (:query)")
    fun searchContacts(query: String): Flowable<List<cat.xlagunas.data.common.db.FriendEntity>>

    @Query("UPDATE friend SET relationship_status = :status WHERE user_id = :userId AND id = :contactId")
    fun update(userId: Long, contactId: Long, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friendDbEntityList: List<cat.xlagunas.data.common.db.FriendEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friendDbEntity: cat.xlagunas.data.common.db.FriendEntity)

    @Delete
    fun delete(friendDbEntity: cat.xlagunas.data.common.db.FriendEntity)

    @Delete
    fun delete(friendDbEntityList: List<cat.xlagunas.data.common.db.FriendEntity>)

    @Query("DELETE FROM friend")
    fun deleteData()

}