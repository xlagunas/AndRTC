package cat.xlagunas.viv.contact

import cat.xlagunas.data.contact.list.ContactRepositoryImpl
import cat.xlagunas.data.contact.list.ContactsApi
import cat.xlagunas.domain.contact.list.ContactRepository
import dagger.MapKey
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



}