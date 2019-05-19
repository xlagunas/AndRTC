package cat.xlagunas.contact.di

import cat.xlagunas.call.CallApi
import cat.xlagunas.call.CallRepository
import cat.xlagunas.call.CallRepositoryImpl
import cat.xlagunas.contact.data.ContactCacheImpl
import cat.xlagunas.contact.data.ContactRepositoryImpl
import cat.xlagunas.contact.data.ContactsApi
import cat.xlagunas.contact.domain.ContactCache
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.core.data.db.UserDao
import cat.xlagunas.core.data.db.VivDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class ContactModule {

    @Provides
    fun provideContactRepository(contactRepository: ContactRepositoryImpl): ContactRepository {
        return contactRepository
    }

    @Provides
    fun provideContactsApi(retrofit: Retrofit): ContactsApi =
        retrofit.create(ContactsApi::class.java)

    @Provides
    fun provideContactCache(contactCache: ContactCacheImpl): ContactCache {
        return contactCache
    }

    @Provides
    fun provideCallRepository(callRepository: CallRepositoryImpl): CallRepository {
        return callRepository
    }

    @Provides
    fun provideCallApi(retrofit: Retrofit): CallApi =
        retrofit.create(CallApi::class.java)

    @Provides
    // TODO PROBABLY THIS NEEDS TO GO UP IN THE APP GRAPH
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    fun provideUserDao(database: VivDatabase): UserDao {
        return database.userDao()
    }
}