package cat.xlagunas.viv.contact

import cat.xlagunas.call.CallApi
import cat.xlagunas.call.CallRepository
import cat.xlagunas.call.CallRepositoryImpl
import cat.xlagunas.data.contact.ContactCache
import cat.xlagunas.data.contact.ContactCacheImpl
import cat.xlagunas.data.contact.ContactRepositoryImpl
import cat.xlagunas.data.contact.ContactsApi
import cat.xlagunas.data.contact.PhoneContactsDataSourceImpl
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.domain.contact.PhoneContactsDataSource
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
    fun providePhoneContactDataSource(phoneContactsDataSource: PhoneContactsDataSourceImpl): PhoneContactsDataSource {
        return phoneContactsDataSource
    }

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
}