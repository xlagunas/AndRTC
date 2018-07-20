package cat.xlagunas.data.common.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(UserEntity::class, FriendEntity::class), version = 1)
abstract class VivDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendDao(): FriendDao
    // abstract fun callDao(): CallDao
}