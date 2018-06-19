package cat.xlagunas.viv.contact

import android.arch.lifecycle.ViewModel
import cat.xlagunas.domain.contact.list.ContactRepository
import timber.log.Timber
import javax.inject.Inject

class FriendshipViewModel @Inject constructor(private val contactRepository: ContactRepository) : ViewModel() {

    fun addContact(id: Long) {
        Timber.d("Requesting addContact to $id")
        contactRepository.requestFriendship(id)
                .subscribe({ Timber.i("FriendshipRequest successful") }, { Timber.e(it, "FriendshipRequest errpr") })
    }
}