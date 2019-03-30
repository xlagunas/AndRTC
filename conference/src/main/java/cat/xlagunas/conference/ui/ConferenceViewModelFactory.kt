package cat.xlagunas.conference.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

internal class ConferenceViewModelFactory @Inject constructor() : ViewModelProvider.Factory {

    @Inject
    lateinit var conferenceViewModel: ConferenceViewModel

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ConferenceViewModel::class.java)) {
            conferenceViewModel as T
        } else {
            throw IllegalArgumentException(
                "Class ${modelClass.name} is not supported in this factory."
            )
        }
    }
}
