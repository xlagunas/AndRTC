package cat.xlagunas.core.persistence.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, FriendEntity::class], version = 1)
abstract class VivDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendDao(): FriendDao
    // abstract fun callDao(): CallDao
}