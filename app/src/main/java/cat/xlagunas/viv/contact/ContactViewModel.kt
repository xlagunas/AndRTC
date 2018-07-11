package cat.xlagunas.viv.contact

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactRepository
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.extension.toLiveData
import timber.log.Timber
import javax.inject.Inject

class ContactViewModel
@Inject constructor(private val authenticationRepository: AuthenticationRepository,
                    private val contactRepository: ContactRepository) : ViewModel() {


    val contacts by lazy {
        authenticationRepository.getUser()
                .doOnNext { Timber.d("Next user: ${it.username}") }
                .switchMap { contactRepository.getContacts() }.toLiveData()
    }


    fun findContact(searchTerm: String): LiveData<List<Friend>> {
        return contactRepository.searchContact(searchTerm).toFlowable().toLiveData()
    }

}


