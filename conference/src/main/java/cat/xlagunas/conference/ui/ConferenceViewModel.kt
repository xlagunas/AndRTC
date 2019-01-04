package cat.xlagunas.conference.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.model.Conferencee
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.webrtc.EglBase
import timber.log.Timber
import javax.inject.Inject

class ConferenceViewModel @Inject constructor(
    private val conferenceRepository: ConferenceRepository,
    private val eglContext: EglBase.Context
) : ViewModel() {

    private val jobs = mutableListOf<Job>()

    val conferenceAttendees by lazy { MutableLiveData<List<Conferencee>>() }

    val localStream by lazy { MutableLiveData<ProxyVideoSink>() }

    fun getEGLContext(): EglBase.Context {
        return eglContext
    }

    fun onStart() {
        conferenceRepository.joinRoom()

        jobs += GlobalScope.launch(Dispatchers.IO) { conferenceRepository.registerUser() }

        jobs += GlobalScope.launch(Dispatchers.IO) {
            val users = conferenceRepository.getConnectedUsers().receive()
            conferenceAttendees.postValue(users)
            users.forEach {
                val peerConnection = conferenceRepository.createPeerConnection(it.contactId)
                if (peerConnection != null) {
                    //TODO Add local media stream before creating offer
                    conferenceRepository.createOffer(it.contactId)
                } else Timber.w("Couldn't create peer connection, aborting offer sending")
            }
        }

        localStream.postValue(conferenceRepository.getLocalRenderer())
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