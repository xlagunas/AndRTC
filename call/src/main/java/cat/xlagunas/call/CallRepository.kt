package cat.xlagunas.call

import cat.xlagunas.push.Message

interface CallRepository {
    suspend fun createCall(contacts: List<Long>): Call
    suspend fun acceptCall(callId: Long)
    suspend fun rejectCall(callId: Long)
    suspend fun getCallDetails(message: Message): Call
}