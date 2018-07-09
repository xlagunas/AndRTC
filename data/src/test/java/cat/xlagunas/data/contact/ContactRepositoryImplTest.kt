package cat.xlagunas.data.contact

import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.domain.schedulers.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.io.IOException

class ContactRepositoryImplTest {

    lateinit var contactRepository: ContactRepository

    @Mock
    lateinit var localContactDataSource: LocalContactDataSource

    @Mock
    lateinit var remoteContactDataSource: RemoteContactDataSource

    @Mock
    lateinit var phoneContactsDataSource: PhoneContactsDataSourceImpl

    @Mock
    lateinit var cache: ContactCache


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val schedulers = RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline())
        contactRepository = ContactRepositoryImpl(localContactDataSource, remoteContactDataSource, phoneContactsDataSource, cache, schedulers)
    }

    @Test
    fun requestFriendship() {
        val friend = Friend(10, "aUsername", "aName", "aUserImage", "anEmail", "status")
        `when`(remoteContactDataSource.requestFriendship(friend)).thenReturn(Completable.complete())
        `when`(localContactDataSource.requestFriendship(friend)).thenReturn(Completable.complete())
        contactRepository.requestFriendship(friend)
                .test().assertComplete()

        verify(localContactDataSource).requestFriendship(friend)
    }

    @Test
    fun givenCacheIsValid_whenGetContacts_thenNoUpdate() {
        `when`(cache.isCacheValid()).thenReturn(Single.just(true))
        `when`(localContactDataSource.getContacts()).thenReturn(Flowable.empty())

        contactRepository.getContacts().test().assertComplete()

        verify(remoteContactDataSource, never()).getContactsAsSingle()
        verify(cache, never()).updateCache()
    }

    @Test
    fun givenCacheIsInvalid_whenGetContacts_thenUpdate() {
        `when`(cache.isCacheValid()).thenReturn(Single.just(false))
        `when`(localContactDataSource.getContacts()).thenReturn(Flowable.empty())
        `when`(remoteContactDataSource.getContactsAsSingle()).thenReturn(Single.just(emptyList()))
        `when`(localContactDataSource.updateContacts(emptyList())).thenReturn(Completable.complete())

        contactRepository.getContacts().test()

        verify(remoteContactDataSource).getContactsAsSingle()
        verify(cache).updateCache()
    }

    @Test
    fun givenCacheIsInvalid_whenGetContactsError_thenNoCacheUpdate() {
        `when`(cache.isCacheValid()).thenReturn(Single.just(false))
        `when`(remoteContactDataSource.getContactsAsSingle()).thenReturn(Single.error(IOException()))
        `when`(localContactDataSource.getContacts()).thenReturn(Flowable.empty())

        contactRepository.getContacts().test()

        verify(remoteContactDataSource).getContactsAsSingle()
        verify(cache, never()).updateCache()
    }

    @Test
    fun findContacts() {
    }
}