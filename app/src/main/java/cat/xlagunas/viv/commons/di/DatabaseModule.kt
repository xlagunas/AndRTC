package cat.xlagunas.viv.commons.di

import android.arch.persistence.room.Room
import android.content.Context
import cat.xlagunas.data.common.db.FriendDao
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.data.common.db.VivDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by xavier.lagunas on 27/12/17.
 */
@Module
class DatabaseModule {

    @Singleton
    @Provides
    internal fun provideDatabase(applicationContext: Context): VivDatabase {
        return Room.databaseBuilder(applicationContext, VivDatabase::class.java, "database.db").build()
    }

    @Provides
    fun provideUserDao(database: VivDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideFriendDao(database: VivDatabase): FriendDao {
        return database.friendDao()
    }
}