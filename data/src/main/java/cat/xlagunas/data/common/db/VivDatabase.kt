package cat.xlagunas.data.common.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(cat.xlagunas.data.common.db.UserEntity::class, cat.xlagunas.data.common.db.FriendEntity::class), version = 1)
abstract class VivDatabase : RoomDatabase() {
    abstract fun userDao(): cat.xlagunas.data.common.db.UserDao
    abstract fun friendDao(): cat.xlagunas.data.common.db.FriendDao
    //abstract fun callDao(): CallDao
}