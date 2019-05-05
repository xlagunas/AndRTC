package cat.xlagunas.viv.contact.viewholder

import cat.xlagunas.contact.domain.ContactRepository
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.viv.commons.DisposableViewModel
import timber.log.Timber
import javax.inject.Inject

class FriendshipViewModel
@Inject constructor(private val contactRepository: ContactRepository) : DisposableViewModel() {

    fun addContact(friend: Friend) {
        Timber.d("Requesting addContact to ${friend.friendId}")
        disposable.add(
            contactRepository.requestFriendship(friend)
                .subscribe({ Timber.i("FriendshipRequest successful") }, { Timber.e(it, "FriendshipRequest errpr") })
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
                .subscribe({ Timber.i("Rejected contact") }, { Timber.e(it, "RejectContact error") })
        )
    }

    fun callFriend(friend: Friend) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}