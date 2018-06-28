package cat.xlagunas.data.contact

import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.domain.schedulers.RxSchedulers
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ContactRepositoryImplTest {

    lateinit var contactRepository: ContactRepository

    @Mock
    lateinit var localContactDataSource: LocalContactDataSource

    @Mock
    lateinit var remoteContactDataSource: RemoteContactDataSource

    @Mock
    lateinit var phoneContactsDataSource: PhoneContactsDataSourceImpl


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val schedulers = RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline())
        contactRepository = ContactRepositoryImpl(localContactDataSource, remoteContactDataSource, phoneContactsDataSource, schedulers)
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
    fun findContacts() {
    }
}