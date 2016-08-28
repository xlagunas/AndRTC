package cat.xlagunas.andrtc.data.social;

import android.content.Intent;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONObject;

import rx.Observable;

/**
 * Created by xlagunas on 20/8/16.
 */
public interface FacebookManager {
    Observable<AccessToken> login();

    Observable<JSONObject> requestProfileData(AccessToken accessToken);

    Observable<JSONArray> requestFriends(AccessToken accessToken);

    void onActivityResult(int requestCode, int resultCode, Intent data);


}
