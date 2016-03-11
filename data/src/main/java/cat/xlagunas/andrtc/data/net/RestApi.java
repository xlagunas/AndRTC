package cat.xlagunas.andrtc.data.net;

import cat.xlagunas.andrtc.data.net.params.LoginParams;
import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.net.params.TokenParams;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface RestApi {

    @POST("/user/login")
    Observable<UserEntity> loginUser(@Body LoginParams params);

    @PUT("/user")
    Observable<UserEntity> createUser(@Body UserEntity entity);

    @PUT("/user/token")
    Observable addToken(@Header("Authorization") String authorization, @Body TokenParams entity);


}
