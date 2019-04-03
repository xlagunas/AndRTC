package cat.xlagunas.data.call

import io.reactivex.Single
import retrofit2.http.PUT

interface CallApi {
    @PUT
    fun createCall(callParticipants: CallParticipantsDto): Single<CallDto>
}
