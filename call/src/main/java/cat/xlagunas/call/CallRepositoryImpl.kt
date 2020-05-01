package cat.xlagunas.call

import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(
    private val callApi: CallApi,
    private val callConverter: CallConverter
) : CallRepository {
    override suspend fun createCall(contacts: List<Long>): Call {
        val callParticipantsDto = callConverter.toCallParticipantsDto(contacts)
        val callDto = callApi.createCall(callParticipantsDto)
        return callConverter.toCall(callDto)
    }

    override suspend fun acceptCall(callId: Long) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun rejectCall(callId: Long) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}