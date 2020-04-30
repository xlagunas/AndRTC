package cat.xlagunas.contact.ui

import androidx.lifecycle.LiveData
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.common.toLiveData
import cat.xlagunas.core.domain.auth.AuthDataStore
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.core.navigation.Navigator
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class ContactViewModel
@Inject constructor(
    private val contactRepository: ContactRepository,
    private val dataStore: AuthDataStore,
    private val navigator: Navigator
) : DisposableViewModel() {

    init {
        Timber.d("Creating ViewModel")
    }

    val contacts by lazy {
        dataStore.getCurrentUserIdFlowable().toFlowable(BackpressureStrategy.BUFFER)
            .doOnSubscribe { Timber.d("Subscribed to contacts") }
            .doOnNext { Timber.d("Next user id: $it") }
            .switchMap { contactRepository.getContacts() }.toLiveData()
    }

    fun findContact(searchTerm: String): LiveData<List<Friend>> {
        return contactRepository.searchContact(searchTerm).toFlowable().toLiveData()
    }

    fun addContact(friend: Friend) {
        Timber.d("Requesting addContact to ${friend.friendId}")
        disposable.add(
            contactRepository.requestFriendship(friend)
                .subscribe(
                    { Timber.i("FriendshipRequest successful") },
                    { Timber.e(it, "FriendshipRequest error") })
        )
    }

    fun acceptContactRequest(friend: Friend) {
        disposable.add(
            contactRepository.addContact(friend)
                .subscribe({ Timber.i("Added new contact") }, { Timber.e(it, "AddContact error") })
        )
    }

    fun rejectContactRequest(friend: Friend) {
        disposable.add(
            contactRepository.rejectContact(friend)
                .subscribe(
                    { Timber.i("Rejected contact") },
                    { Timber.e(it, "RejectContact error") })
        )
    }

    fun createCall(friends: List<Friend>) {
        val (id, username) = friends.first()
        navigator.requestCall(id, username)
    }

    override fun onCleared() {
        Timber.d("Calling onCleared $this")
        super.onCleared()
    }
}