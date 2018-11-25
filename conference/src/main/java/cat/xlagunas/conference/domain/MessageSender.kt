package cat.xlagunas.conference.domain

interface MessageSender {
    fun sendOffer(offer: SessionMessage)
    fun sendAnswer(answer: SessionMessage)
    fun sendIceCandidate(candidate: IceCandidateMessage)
}