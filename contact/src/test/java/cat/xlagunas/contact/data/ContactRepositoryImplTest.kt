package cat.xlagunas.contact.data

import cat.xlagunas.contact.domain.ContactCache
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.core.scheduler.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ContactRepositoryImplTest {

    lateinit var contactRepository: ContactRepository

    @Mock
    lateinit var localContactDataSource: LocalContactDataSource

    @Mock
    lateinit var remoteContactDataSource: RemoteContactDataSource

    @Mock
    lateinit var cache: ContactCache

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val schedulers =
            RxSchedulers(
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                Schedulers.trampoline()
            )
        contactRepository = ContactRepositoryImpl(
            localContactDataSource,
            remoteContactDataSource,
            cache,
            schedulers
        )
    }

    @Test
    fun requestFriendship() {
        val friend = Friend(
            10,
            "aUsername",
            "aName",
            "aUserImage",
            "anEmail",
            "status"
        )
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
