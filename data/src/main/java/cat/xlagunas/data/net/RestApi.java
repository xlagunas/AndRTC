package cat.xlagunas.data.net;

import cat.xlagunas.data.UserEntity;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;
import cat.xlagunas.data.net.params.LoginParams;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface RestApi {

    @POST("/user/login")
    Observable<UserEntity> loginUser(@Body LoginParams params);

    @PUT("/user")
    Observable<UserEntity> createUser(@Body UserEntity entity);


}
