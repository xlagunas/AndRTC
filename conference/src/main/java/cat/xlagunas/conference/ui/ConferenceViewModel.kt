package cat.xlagunas.conference.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.webrtc.EglBase
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class ConferenceViewModel @Inject constructor(
    private val conferenceRepository: ConferenceRepository,
    private val eglContext: EglBase.Context
) : ViewModel() {

    private val jobs = mutableListOf<Job>()

    val conferenceAttendees by lazy { MutableLiveData<Int>() }

    val localStream by lazy { MutableLiveData<ProxyVideoSink>() }
    val remoteStream by lazy { MutableLiveData<ProxyVideoSink>() }

    private val totalConnectedUsers = AtomicInteger()

    fun getEGLContext(): EglBase.Context {
        return eglContext
    }

    fun onStart() {
        conferenceRepository.joinRoom()

        jobs += GlobalScope.launch(Dispatchers.IO) {
           conferenceRepository.onNewUser().consumeEach {
               conferenceAttendees.postValue(totalConnectedUsers.incrementAndGet())
                val peerConnection = conferenceRepository.createPeerConnection(it.userId)
                if (peerConnection != null) {
                    //TODO Add local media stream before creating offer
                    conferenceRepository.createOffer(it.userId)
                } else Timber.w("Couldn't create peer connection, aborting offer sending")
            }
        }

        localStream.postValue(conferenceRepository.getLocalRenderer())
        remoteStream.postValue(conferenceRepository.getRemoteRenderer())
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