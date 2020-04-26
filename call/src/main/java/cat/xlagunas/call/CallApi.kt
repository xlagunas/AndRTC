package cat.xlagunas.call

import retrofit2.http.Body
import retrofit2.http.PUT

interface CallApi {
    @PUT("/call/")
    suspend fun createCall(@Body callParticipants: CallParticipantsDto): CallDto
}
