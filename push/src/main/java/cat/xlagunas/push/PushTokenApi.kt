package cat.xlagunas.push

import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.PUT

interface PushTokenApi {
    @PUT("/token/")
    fun addPushToken(@Body token: PushTokenDto): Completable
}