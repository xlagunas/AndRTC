package cat.xlagunas.andrtc.data.net;

import cat.xlagunas.andrtc.data.net.params.TokenParams;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import rx.Observable;
import retrofit2.http.PUT;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Header;
import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.net.params.LoginParams;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface RestApi {

    @POST("/user/login")
    Observable<UserEntity> loginUser(@Body LoginParams params);

    @PUT("/user")
    Observable<UserEntity> createUser(@Body UserEntity entity);

    @PUT("/user/token")
    Observable<Response<Void>> addToken(@Header("Authorization") String authorization, @Body TokenParams tokenParams);


}
