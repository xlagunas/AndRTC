package cat.xlagunas.data.user.register

import cat.xlagunas.data.common.net.UserDto
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT

interface RegisterApi {

    @PUT("/user/")
    fun registerUser(@Body userDto: UserDto): Completable

}