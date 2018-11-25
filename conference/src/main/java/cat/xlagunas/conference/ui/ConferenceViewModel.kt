package cat.xlagunas.conference.ui

import androidx.lifecycle.ViewModel
import cat.xlagunas.conference.domain.ConferenceRepository
import javax.inject.Inject

class ConferenceViewModel @Inject constructor(private val conferenceRepository: ConferenceRepository) : ViewModel() {

    fun onStart() {
        conferenceRepository.joinRoom()
    }

    override fun onCleared() {
        conferenceRepository.logoutRoom()
    }
}