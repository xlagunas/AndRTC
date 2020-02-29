package cat.xlagunas.contact.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.contact.ui.ContactViewModel
import javax.inject.Inject

internal class ContactViewModelFactory @Inject constructor() : ViewModelProvider.Factory {

    @Inject
    lateinit var contactViewModel: ContactViewModel

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            contactViewModel as T
        } else {
            throw IllegalArgumentException(
                "Class ${modelClass.name} is not supported in this factory."
            )
        }
    }
}
