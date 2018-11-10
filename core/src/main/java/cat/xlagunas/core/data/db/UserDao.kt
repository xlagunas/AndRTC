package cat.xlagunas.core.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface UserDao {

    @Query("SELECT COUNT(*) FROM user")
    fun getUserCount(): Flowable<Int>

    @get:Query("SELECT * FROM user LIMIT 1")
    val user: Maybe<UserEntity>

    @Query("SELECT * FROM user LIMIT 1")
    fun getUserHot(): Flowable<UserEntity>

    @Query("SELECT * FROM user")
    fun loadAll(): Flowable<UserEntity>

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    fun loadAllByUserId(vararg userIds: Int): List<UserEntity>

    @Insert
    fun insertAll(users: List<UserEntity>)

    @Insert
    fun insert(userDbEntity: UserEntity)

    @Delete
    fun delete(user: UserEntity)

    @Query("DELETE FROM user")
    fun deleteData()
}
