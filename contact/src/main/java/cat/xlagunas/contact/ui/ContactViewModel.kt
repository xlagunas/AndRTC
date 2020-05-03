package cat.xlagunas.contact.ui

import androidx.lifecycle.LiveData
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.DisposableViewModel
import cat.xlagunas.core.common.toLiveData
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.core.persistence.AuthDataStore
import io.reactivex.BackpressureStrategy
import javax.inject.Inject
import timber.log.Timber

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

    val contacts: LiveData<Result<List<Friend>>> by lazy {
        dataStore.getCurrentUserIdFlowable().toFlowable(BackpressureStrategy.BUFFER)
            .doOnSubscribe { Timber.d("Subscribed to contacts") }
            .doOnNext { Timber.d("Next user id: $it") }
            .switchMap { contactRepository.getContacts() }
            .map { friends -> Result.success(friends) }
            .onErrorReturn { Result.failure(it) }
            .toLiveData()
    }

    fun findContact(searchTerm: String): LiveData<Result<List<Friend>>> {
        return contactRepository.searchContact(searchTerm).toFlowable()
            .map { searchElements -> Result.success(searchElements) }
            .onErrorReturn { Result.failure(it) }
            .toLiveData()
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
