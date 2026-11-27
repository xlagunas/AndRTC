package cat.xlagunas.contact.data

import cat.xlagunas.contact.domain.ContactCache
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.core.scheduler.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.io.IOException

class ContactRepositoryImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    lateinit var contactRepository: ContactRepository

    @Mock
    lateinit var localContactDataSource: LocalContactDataSource

    @Mock
    lateinit var remoteContactDataSource: RemoteContactDataSource

    @Mock
    lateinit var cache: ContactCache

    @Before
    fun setUp() {
        val schedulers =
            RxSchedulers(
                Schedulers.trampoline(),
                Schedulers.trampoline(),
                Schedulers.trampoline(),
            )
        contactRepository =
            ContactRepositoryImpl(
                localContactDataSource,
                remoteContactDataSource,
                cache,
                schedulers,
            )
    }

    @Test
    fun givenFriend_whenRequestFriendship_thenRemoteAndLocalRequested() {
        val friend = friend()
        val localSubject = CompletableSubject.create()
        `when`(remoteContactDataSource.requestFriendship(friend)).thenReturn(Completable.complete())
        `when`(localContactDataSource.requestFriendship(friend)).thenReturn(localSubject)

        val observer = contactRepository.requestFriendship(friend).test()

        assertThat(localSubject.hasObservers()).isTrue()
        observer.assertNotComplete()
        localSubject.onComplete()
        observer.assertComplete()

        val inOrder = inOrder(remoteContactDataSource, localContactDataSource)
        inOrder.verify(remoteContactDataSource).requestFriendship(friend)
        inOrder.verify(localContactDataSource).requestFriendship(friend)
    }

    @Test
    fun givenRemoteRequestFails_whenRequestFriendship_thenLocalNotSubscribed() {
        val friend = friend()
        val localSubject = CompletableSubject.create()
        `when`(remoteContactDataSource.requestFriendship(friend)).thenReturn(Completable.error(IOException()))
        `when`(localContactDataSource.requestFriendship(friend)).thenReturn(localSubject)

        contactRepository.requestFriendship(friend).test().assertError(IOException::class.java)

        assertThat(localSubject.hasObservers()).isFalse()
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
        val remoteFriends = listOf(friend(1), friend(2))
        val localFriends = listOf(friend(3))
        `when`(cache.isCacheValid()).thenReturn(Single.just(false))
        `when`(remoteContactDataSource.getContactsAsSingle()).thenReturn(Single.just(remoteFriends))
        `when`(localContactDataSource.updateContacts(remoteFriends)).thenReturn(Completable.complete())
        `when`(cache.updateCache()).thenReturn(Completable.complete())
        `when`(localContactDataSource.getContacts()).thenReturn(Flowable.just(localFriends))

        contactRepository.getContacts().test()
            .assertValue(localFriends)
            .assertComplete()

        verify(remoteContactDataSource).getContactsAsSingle()
        verify(localContactDataSource).updateContacts(remoteFriends)
        verify(cache).updateCache()
    }

    @Test
    fun givenCacheIsInvalid_whenGetContactsError_thenNoCacheUpdate() {
        `when`(cache.isCacheValid()).thenReturn(Single.just(false))
        `when`(remoteContactDataSource.getContactsAsSingle()).thenReturn(Single.error(IOException()))
        `when`(localContactDataSource.getContacts()).thenReturn(Flowable.just(listOf(friend())))

        contactRepository.getContacts().test()
            .assertValue(listOf(friend()))
            .assertComplete()

        verify(remoteContactDataSource).getContactsAsSingle()
        verify(localContactDataSource, never()).updateContacts(anyList())
        verify(cache, never()).updateCache()
    }

    @Test
    fun givenRemoteResults_whenSearchContact_thenEmitsResults() {
        `when`(remoteContactDataSource.searchContacts("aQuery")).thenReturn(Single.just(listOf(friend())))

        contactRepository.searchContact("aQuery").test()
            .assertValue(listOf(friend()))
            .assertComplete()

        verify(remoteContactDataSource).searchContacts("aQuery")
    }

    private fun friend(id: Long = 10) =
        Friend(
            id,
            "aUsername",
            "aName",
            "aUserImage",
            "anEmail",
            "status",
        )
}
