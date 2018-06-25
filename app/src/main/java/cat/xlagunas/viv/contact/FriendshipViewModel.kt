package cat.xlagunas.viv.contact

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.ViewModel
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactRepository
import timber.log.Timber
import javax.inject.Inject

class FriendshipViewModel
@Inject constructor(private val contactRepository: ContactRepository) : ViewModel() {

    fun addContact(friend: Friend) {
        Timber.d("Requesting addContact to ${friend.friendId}")
        contactRepository.requestFriendship(friend)
                .subscribe({ Timber.i("FriendshipRequest successful") }, { Timber.e(it, "FriendshipRequest errpr") })
    }

    fun acceptContactRequest(friend: Friend) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun rejectContactRequest(friend: Friend) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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