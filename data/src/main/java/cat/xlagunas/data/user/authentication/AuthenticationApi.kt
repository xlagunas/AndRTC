package cat.xlagunas.data.user.authentication

import cat.xlagunas.data.common.net.UserDto
import cat.xlagunas.domain.user.authentication.AuthenticationCredentials
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthenticationApi {

    @PUT("/user/")
    fun registerUser(@Body userDto: UserDto): Completable

    @GET("/user/")
    fun getUser(): Single<UserDto>

    @POST("/auth/")
    fun loginUser(@Body authCredentials: AuthenticationCredentials): Single<AuthTokenDto>

}