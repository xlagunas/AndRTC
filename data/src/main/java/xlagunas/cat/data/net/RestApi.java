package xlagunas.cat.data.net;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;
import xlagunas.cat.data.UserEntity;
import xlagunas.cat.data.net.params.LoginParams;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface RestApi {

    @POST("/user/login")
    Observable<UserEntity> loginUser(@Body LoginParams params);

    @PUT("/user")
    Observable<Response> createUser(@Header("Authorization") String authorization, UserEntity entity);


}
