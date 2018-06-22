package cat.xlagunas.viv.contact.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.ViewModel
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.contact.ContactRepository
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val contactsRepository: ContactRepository) : ViewModel() {

    fun findContact(searchTerm: String): LiveData<List<Friend>> {
        return LiveDataReactiveStreams.fromPublisher(contactsRepository.searchContact(searchTerm).toFlowable())
    }
}