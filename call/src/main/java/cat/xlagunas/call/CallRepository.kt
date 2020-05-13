package cat.xlagunas.call

interface CallRepository {
    suspend fun createCall(contacts: List<Long>): Call
    suspend fun acceptCall(callId: Long)
    suspend fun rejectCall(callId: Long)
}
