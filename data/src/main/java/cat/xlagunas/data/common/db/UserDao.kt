package cat.xlagunas.data.common.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface UserDao {

    @get:Query("SELECT * FROM user LIMIT 1")
    val user: Maybe<cat.xlagunas.data.common.db.UserEntity>

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
