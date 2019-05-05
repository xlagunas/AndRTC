package cat.xlagunas.viv.contact

import androidx.lifecycle.LiveData
import cat.xlagunas.call.Call
import cat.xlagunas.call.CallRepository
import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.user.domain.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import cat.xlagunas.viv.commons.extension.toLiveData
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class ContactViewModel
@Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val contactRepository: ContactRepository,
    private val callRepository: CallRepository
) : DisposableViewModel() {

    val contacts by lazy {
        authenticationRepository.getUser()
            .doOnNext { Timber.d("Next user: ${it.username}") }
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

    fun observeCall(friends: List<Friend>): LiveData<Call> {
        return callRepository.createCall(friends).toFlowable().toLiveData()
    }
}