package cat.xlagunas.conference.domain

import kotlinx.coroutines.channels.ReceiveChannel

interface MessageReceiver {
    fun onSessionMessageReceived(): ReceiveChannel<SessionMessage>
    fun onIceCandidateReceived(): ReceiveChannel<IceCandidateMessage>
}