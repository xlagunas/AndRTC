package cat.xlagunas.viv.contact

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.util.Log
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import timber.log.Timber
import javax.inject.Inject

class FriendshipViewModel
@Inject constructor(private val contactRepository: ContactRepository) : DisposableViewModel() {

    fun addContact(friend: Friend) {
        Timber.d("Requesting addContact to ${friend.friendId}")
        disposable.add(contactRepository.requestFriendship(friend)
                .subscribe({ Timber.i("FriendshipRequest successful") }, { Timber.e(it, "FriendshipRequest errpr") }))
    }

    fun acceptContactRequest(friend: Friend) {
        disposable.add(contactRepository.addContact(friend)
                .subscribe({ Timber.i("Added new contact") }, { Timber.e(it, "AddContact error") }))
    }

    fun rejectContactRequest(friend: Friend) {
        disposable.add(contactRepository.rejectContact(friend)
                .subscribe({ Timber.i("Rejected contact") }, { Timber.e(it, "RejectContact error") }))
    }

    val getContacts by lazy {
        LiveDataReactiveStreams.fromPublisher(contactRepository.getContacts())
    }

    fun findContact(searchTerm: String): LiveData<List<Friend>> {
        return LiveDataReactiveStreams.fromPublisher(contactRepository.searchContact(searchTerm).toFlowable())
    }

    fun callFriend(friend: Friend) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}