package cat.xlagunas.conference.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.model.Conferencee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConferenceViewModel @Inject constructor(private val conferenceRepository: ConferenceRepository) : ViewModel() {

    private val jobs = mutableListOf<Job>()

    val conferenceAttendees by lazy { MutableLiveData<List<Conferencee>>() }

    fun onStart() {
        conferenceRepository.joinRoom()

        jobs += GlobalScope.launch(Dispatchers.IO) {
            val conferencees = conferenceRepository.getConnectedUsers().receive()
            conferenceAttendees.postValue(conferencees)
        }
    }

    override fun onCleared() {
        conferenceRepository.logoutRoom()
        jobs.forEach {
            if (it.isActive) {
                it.cancel()
            }
        }
    }
}