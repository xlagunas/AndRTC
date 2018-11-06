package cat.xlagunas.conference.ui

import androidx.lifecycle.ViewModel
import cat.xlagunas.conference.data.ConferenceRepositoryImp
import cat.xlagunas.conference.domain.ConferenceRepository

class ConferenceViewModel : ViewModel(){


    private val conferenceRepository: ConferenceRepository = ConferenceRepositoryImp()

    fun onStart(){
        conferenceRepository.joinRoom("anyString")
    }


    override fun onCleared() {
        conferenceRepository.logoutRoom()
    }
}