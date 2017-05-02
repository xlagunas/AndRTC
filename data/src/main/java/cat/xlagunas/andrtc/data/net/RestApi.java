package cat.xlagunas.andrtc.data.net;

import java.util.List;

import cat.xlagunas.andrtc.data.FriendEntity;
import cat.xlagunas.andrtc.data.net.params.TokenParams;
import cat.xlagunas.andrtc.data.net.params.UpdateParams;
import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
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

    @GET("user/profile")
    Observable<UserEntity> pullProfile(@Header("Authorization") String authorization);

    @PUT("/user")
    Observable<UserEntity> createUser(@Body UserEntity entity);

    @PUT("/user/social/fb")
    Observable<UserEntity> createFBUser(@Body UserEntity entity);

    @PUT("/user/social/google")
    Observable<UserEntity> createGoogleUser(@Body UserEntity entity);

    @PUT("/user/token")
    Observable<UserEntity> addToken(@Header("Authorization") String authorization, @Body TokenParams tokenParams);

    @GET("/user/{username}")
    Observable<List<FriendEntity>> findUsers(@Header("Authorization") String authorization, @Path("username") String username);

    @PUT("/friendship/{id}")
    Observable<UserEntity> requestFriendship(@Header("Authorization") String authorization, @Path("id") String id);

    @POST("/friendship/update")
    Observable<UserEntity> updateFriendship(@Header("Authorization") String authorization, @Body UpdateParams updateParams);

    @POST("/user/image")
    @Multipart
    Observable<UserEntity> putUserProfilePicture(@Header("Authorization") String authorization, @Part MultipartBody.Part file);

    @POST("user/call/{id}")
    Observable<Void> requestCallUser(@Header("Authorization") String authorization, @Path("id") String friendId);

    @POST("user/call/{id}/accept")
    Observable<Void> acceptCallUser(@Header("Authorization") String authorization, @Path("id") String callId);

    @POST("user/call/{id}/reject")
    Observable<Void> cancelCallUser(@Header("Authorization") String authorization, @Path("id") String callId);

}
