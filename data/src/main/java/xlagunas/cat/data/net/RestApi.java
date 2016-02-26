package xlagunas.cat.data.net;

import retrofit2.Response;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;
import xlagunas.cat.data.UserEntity;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface RestApi {

    @POST("/user/login")
    Observable<UserEntity> loginUser(String username, String password);

    @PUT("/user")
    Observable<Response> createUser(UserEntity entity);


}
