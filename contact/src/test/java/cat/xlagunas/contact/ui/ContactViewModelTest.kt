package cat.xlagunas.contact.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.core.persistence.AuthDataStore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.IOException

class ContactViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val contactRepository = mock(ContactRepository::class.java)
    private val dataStore = mock(AuthDataStore::class.java)
    private val navigator = mock(Navigator::class.java)

    private val currentUserId = BehaviorSubject.create<Long>()
    private lateinit var viewModel: ContactViewModel

    private val friend = Friend(10, "aFriend", "A Friend", null, "friend@example.com", "ACCEPTED")
    private val otherFriend = friend.copy(friendId = 11, username = "otherFriend")

    @Before
    fun setUp() {
        `when`(dataStore.getCurrentUserIdFlowable()).thenReturn(currentUserId)
        viewModel = ContactViewModel(contactRepository, dataStore, navigator)
    }

    @Test
    fun givenLoggedUser_whenObservingContacts_thenContactsEmitted() {
        `when`(contactRepository.getContacts()).thenReturn(Flowable.just(listOf(friend)))
        val emitted = observeContacts()

        currentUserId.onNext(1L)

        assertThat(emitted.map { it.getOrThrow() }).containsExactly(listOf(friend))
    }

    @Test
    fun givenNoLoggedUser_whenObservingContacts_thenNothingEmitted() {
        val emitted = observeContacts()

        assertThat(emitted).isEmpty()
    }

    @Test
    fun givenUserChanges_whenObservingContacts_thenContactsReloaded() {
        `when`(contactRepository.getContacts())
            .thenReturn(Flowable.just(listOf(friend)), Flowable.just(listOf(otherFriend)))
        val emitted = observeContacts()

        currentUserId.onNext(1L)
        currentUserId.onNext(2L)

        assertThat(emitted.map { it.getOrThrow() }).containsExactly(listOf(friend), listOf(otherFriend))
    }

    @Test
    fun givenRepositoryError_whenObservingContacts_thenFailureEmitted() {
        val error = IOException("No connection")
        `when`(contactRepository.getContacts()).thenReturn(Flowable.error(error))
        val emitted = observeContacts()

        currentUserId.onNext(1L)

        assertThat(emitted).hasSize(1)
        assertThat(emitted.first().exceptionOrNull()).isSameAs(error)
    }

    @Test
    fun givenSearchSucceeds_whenFindContact_thenResultsEmitted() {
        `when`(contactRepository.searchContact("fri")).thenReturn(Single.just(listOf(friend, otherFriend)))
        val emitted = mutableListOf<Result<List<Friend>>>()

        viewModel.findContact("fri").observeForever { emitted.add(it) }

        assertThat(emitted.map { it.getOrThrow() }).containsExactly(listOf(friend, otherFriend))
    }

    @Test
    fun givenSearchFails_whenFindContact_thenFailureEmitted() {
        val error = IOException("No connection")
        `when`(contactRepository.searchContact("fri")).thenReturn(Single.error(error))
        val emitted = mutableListOf<Result<List<Friend>>>()

        viewModel.findContact("fri").observeForever { emitted.add(it) }

        assertThat(emitted).hasSize(1)
        assertThat(emitted.first().exceptionOrNull()).isSameAs(error)
    }

    @Test
    fun whenAddContact_thenFriendshipRequested() {
        var requested = false
        `when`(contactRepository.requestFriendship(friend)).thenReturn(Completable.fromAction { requested = true })

        viewModel.addContact(friend)

        assertThat(requested).isTrue()
    }

    @Test
    fun whenAcceptContactRequest_thenContactAdded() {
        var accepted = false
        `when`(contactRepository.addContact(friend)).thenReturn(Completable.fromAction { accepted = true })

        viewModel.acceptContactRequest(friend)

        assertThat(accepted).isTrue()
    }

    @Test
    fun whenRejectContactRequest_thenContactRejected() {
        var rejected = false
        `when`(contactRepository.rejectContact(friend)).thenReturn(Completable.fromAction { rejected = true })

        viewModel.rejectContactRequest(friend)

        assertThat(rejected).isTrue()
    }

    @Test
    fun givenRepositoryError_whenAddContact_thenErrorHandledBySubscriber() {
        val unhandledErrors = mutableListOf<Throwable>()
        RxJavaPlugins.setErrorHandler { unhandledErrors.add(it) }
        `when`(contactRepository.requestFriendship(friend)).thenReturn(Completable.error(IOException()))

        try {
            viewModel.addContact(friend)
        } finally {
            RxJavaPlugins.reset()
        }

        assertThat(unhandledErrors).isEmpty()
    }

    @Test
    fun whenCreateCall_thenRequestCallWithFirstFriend() {
        viewModel.createCall(listOf(friend, otherFriend))

        verify(navigator).requestCall(friend.friendId, friend.username)
    }

    private fun observeContacts(): List<Result<List<Friend>>> {
        val emitted = mutableListOf<Result<List<Friend>>>()
        viewModel.contacts.observeForever { emitted.add(it) }
        return emitted
    }
}
