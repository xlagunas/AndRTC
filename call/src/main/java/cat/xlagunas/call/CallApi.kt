package cat.xlagunas.call

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.PUT

interface CallApi {
    @PUT("/call/")
    fun createCall(@Body callParticipants: CallParticipantsDto): Single<CallDto>
}
