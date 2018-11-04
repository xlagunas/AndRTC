package cat.xlagunas.data.common.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(UserEntity::class, FriendEntity::class), version = 1)
abstract class VivDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendDao(): FriendDao
    // abstract fun callDao(): CallDao
}