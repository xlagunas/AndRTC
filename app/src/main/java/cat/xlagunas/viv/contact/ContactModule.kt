package cat.xlagunas.viv.contact

import cat.xlagunas.data.contact.*
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.domain.contact.PhoneContactsDataSource
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ContactModule {

    @Provides
    @Singleton
    fun provideContactRepository(contactRepository: ContactRepositoryImpl): ContactRepository {
        return contactRepository
    }

    @Provides
    @Singleton
    fun provideContactsApi(retrofit: Retrofit): ContactsApi = retrofit.create(ContactsApi::class.java)

    @Provides
    @Singleton
    fun providePhoneContactDataSource(phoneContactsDataSource: PhoneContactsDataSourceImpl): PhoneContactsDataSource {
        return phoneContactsDataSource
    }

    @Provides
    @Singleton
    fun provideContactCache(contactCache: ContactCacheImpl): ContactCache {
        return contactCache
    }

}