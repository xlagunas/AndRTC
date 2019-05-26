package dagger

import android.content.Context
import androidx.room.Room
import cat.xlagunas.core.data.db.FriendDao
import cat.xlagunas.core.data.db.VivDatabase

/**
 * Created by xavier.lagunas on 27/12/17.
 */
@Module
class DatabaseModule {

    @Provides
    internal fun provideDatabase(applicationContext: Context): VivDatabase {
        return Room.databaseBuilder(applicationContext, VivDatabase::class.java, "database.db").build()
    }

    @Provides
    fun provideFriendDao(database: VivDatabase): FriendDao {
        return database.friendDao()
    }
}